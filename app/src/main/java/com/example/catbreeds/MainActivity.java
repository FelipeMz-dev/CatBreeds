package com.example.catbreeds;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catbreeds.ListView.ListAdapter;
import com.example.catbreeds.ListView.ListElement;
import com.example.catbreeds.services.ApiHelper;
import com.example.catbreeds.services.ApiResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<ListElement> elements; //Elements (CardView) to ListAdapter
    ListAdapter listAdapter;  //Adapter for recyclerView
    JSONArray breeds = new JSONArray(); //Api catBreeds Data in JSONArray
    JSONObject images = new JSONObject(); //Api with image url for catsBreeds
    HashMap<String, String> flags = new HashMap<>(); //contain the flag emojis
    RecyclerView recyclerView; //contain the recyclerView to View
    private static final String URL_API_FLAGS = "https://raw.githubusercontent.com/matiassingers/emoji-flags/master/data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        elements = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewBreeds);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        getCountryFlags();
        getDataApi();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(query);
                searchView.onActionViewCollapsed();
                searchView.setSubmitButtonEnabled(false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "check", Toast.LENGTH_SHORT).show();
                searchView.setSubmitButtonEnabled(true);
                if (searchView.isSubmitButtonEnabled()){
                    //restore search
                    listAdapter.setItems(elements);
                    recyclerView.getRecycledViewPool().clear();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
        return true;
    }
    /**
     * Writhe the result of search in #queryElements and apply the changes in {@link #recyclerView}
     * @param query get the query of #searchView on submit
     * **/
    private void onSearch(String query){
        List<ListElement> queryElements = new ArrayList<>();
        String formatQuery = query.toLowerCase().trim();

        for (ListElement element:elements){
            if (element.getNameBreed().toLowerCase().contains(formatQuery)){
                queryElements.add(element); //search for name
            }else if (element.getOriginBreed().toLowerCase().contains(formatQuery)){
                queryElements.add(element);//search for origin
            }
        }
        Toast.makeText(this, String.valueOf(queryElements.size())+" Results", Toast.LENGTH_SHORT).show();
        // reload the recycler view
        listAdapter.setItems(queryElements);
        recyclerView.getRecycledViewPool().clear();
        listAdapter.notifyDataSetChanged();
    }
    /**
     * load breed data for api service and assign in JSONArray {@link #breeds}
     * @see ApiHelper#GetData() -Servicio API used
     * **/
    public void getDataApi() {
        ApiHelper api = new ApiHelper(getApplicationContext());
        api.getResult().observe(
                this,
                new Observer<ApiResult>() {
                    @Override
                    public void onChanged(ApiResult apiResult) {
                        if (apiResult == null) {
                            api.GetData();
                            return;
                        }
                        breeds = apiResult.getArrayResult();
                        fillCards();
                    }
                });
        api.GetData();
    }
    /**
     * get String of flags emojis for api services and save in {@link #flags}.
     * #urlApiFla have the url to api service where are the lags emojis
     * @see ApiHelper#GetFlags(String)
     * **/
    public void getCountryFlags() {
        ApiHelper api = new ApiHelper(getApplicationContext());
        api.getResult().observe(
                this,
                new Observer<ApiResult>() {
                    @Override
                    public void onChanged(ApiResult apiResult) {
                        JSONArray arrayFlags = apiResult.getArrayResult();
                        JSONObject object;
                        for (int i = 0; i <= arrayFlags.length() - 1; i++) {
                            try {
                                object = arrayFlags.getJSONObject(i);
                                flags.put(object.getString("name").trim(), object.getString("emoji"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        flags.put("Burma", flags.get("Myanmar"));
                    }
                }
        );
        api.GetFlags(URL_API_FLAGS);
    }
    /**
     * Load the breeds images from URL in api service and writhe in JSONObject {@link #images}
     * @param referenceImage ID reference by image from api service
     * @param id set the id of breed to writhe in JSONObject
     * @param finalize when process end, do {@link #loadImages()}
     * @see ApiHelper#GetData() API service used
     * **/
    public void getImageCat(String referenceImage, String id, boolean finalize) {

        ApiHelper api = new ApiHelper(getApplicationContext());
        api.getResult().observe(
                this,
                new Observer<ApiResult>() {
                    @Override
                    public void onChanged(ApiResult apiResult) {
                        if (finalize) loadImages();
                        if (apiResult == null) return;
                        try {
                            JSONObject object = apiResult.getObjResult();
                            String url = object.getString("url");
                            images.put(id, url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        api.GetImageBreed(referenceImage);

    }
    /**
     * fill and show recyclerView with {@link #listAdapter}
     * then load the URL images for {@link #getImageCat(String, String, boolean)}
     * **/
    public void fillCards() {
        Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
        String nameBreed, urlBreed, originBreed, descriptionBreed, temperamentBreed;
        for (int i = 0; i <= breeds.length() - 1; i++) {
            try {
                JSONObject object = breeds.getJSONObject(i);
                nameBreed = object.has("name") ? object.getString("name") : null;
                Log.e("index", nameBreed);
                urlBreed = object.has("wikipedia_url") ? object.getString("wikipedia_url") : null;
                originBreed = object.has("origin") ? object.getString("origin").trim() : null;
                descriptionBreed = object.has("description") ? "Description:\n" + object.getString("description") : null;
                temperamentBreed = object.has("temperament") ? "Temperament:\n" + object.getString("temperament") : null;
                if (flags.containsKey(originBreed)) originBreed += " " + flags.get(originBreed);
                originBreed = "Origin: " + originBreed;
                int adaptabilityBreed = object.has("adaptability") ? object.getInt("adaptability") : 0;
                int energyBreed = object.has("adaptability") ? object.getInt("adaptability") : 0;
                int intelligenceBreed = object.has("intelligence") ? object.getInt("intelligence") : 0;
                elements.add(new ListElement(
                        nameBreed,
                        originBreed,
                        descriptionBreed,
                        urlBreed,
                        temperamentBreed,
                        null, //without image
                        adaptabilityBreed,
                        energyBreed,
                        intelligenceBreed));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listAdapter = new ListAdapter(elements, this,
                new ListAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(ListElement item) {
                        openWikiBreed(item);
                    }
                });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        for (int i = 0; i <= breeds.length() - 1; i++) {
            boolean finalize = i == breeds.length() - 1;
            try {
                JSONObject object = breeds.getJSONObject(i);
                String referenceImageId = object.getString("reference_image_id");
                String id = object.getString("id");
                getImageCat(referenceImageId, id, finalize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Open the breed of cat in WikiPedia web page
     * @param item get the item that contain the url
     * **/
    public void openWikiBreed(ListElement item) {
        String url = item.getUrlBreed();
        Uri url_wiki = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, url_wiki);
        startActivity(intent);
    }
    /**
     * writhe the images of url in {@link #images}.
     * Use #Thread to do async process
     * **/
    public void loadImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= breeds.length() - 1; i++) {
                    JSONObject object = breeds.optJSONObject(i);
                    try {
                        String urlImageBreed = images.getString(object.getString("id"));
                        URL url = new URL(urlImageBreed);
                        InputStream inputStream = url.openConnection().getInputStream();
                        Bitmap imageBreed = BitmapFactory.decodeStream(inputStream);
                        elements.get(i).setImgBreed(imageBreed);
                        listAdapter.setItems(elements);
                        int finalI = i;
                        recyclerView.post(new Runnable()
                        {
                            @Override
                            public void run() {
                                listAdapter.notifyItemChanged(finalI);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}