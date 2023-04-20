package com.example.catbreeds;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        elements = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewBreeds);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getCountryFlags();
        getDataApi();
    }
    /**
     * cargue los datos de raza para el servicio api y asigne en JSONArray {@link #breeds}
     * @see ApiHelper#GetData() -Servicio API utilizado
     * **/
    public void getDataApi() {
        ApiHelper api = new ApiHelper(getApplicationContext());
        api.getResult().observe(
                this,
                new Observer<ApiResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
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
        String urlApiFlags = "https://raw.githubusercontent.com/matiassingers/emoji-flags/master/data.json";
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
        api.GetFlags(urlApiFlags);
    }
    /**
     * cargue las imágenes de URL para razas en el servicio api y asigne en JSONObject {@link #images}
     * @param referenceImage ID de referencia de la imagen en el servicio api
     * @param id obtener el id de la raza para que coincida con la imagen de referencia en JSONObject
     * @param finalize finaliza la carga de imágenes para el servicio api e inicializa el {@link #loadImages()}
     * @see ApiHelper#GetData() -Servicio API utilizado
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
                            Log.e("image", id + " - " + url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        api.GetImageBreed(referenceImage);

    }
    /**
     * complete y muestre el recyclerView con {@link #listAdapter}
     * luego cargue las imágenes de URL para {@link #getImageCat(String, String, boolean)}
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
     * carga las imágenes desde la url en {@link #images}.
     * Use #Thread para hacer un proceso asíncrono
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