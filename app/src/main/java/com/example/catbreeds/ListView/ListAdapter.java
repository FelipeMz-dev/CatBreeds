package com.example.catbreeds.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.catbreeds.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<ListElement> elementData;
    private LayoutInflater elementInflater;
    private Context context;
    final ListAdapter.onItemClickListener listener;

    public interface onItemClickListener{
        void onItemClick(ListElement item);
    }

    public ListAdapter(List<ListElement> itemList, Context context, ListAdapter.onItemClickListener listener){
        this.elementData = itemList;
        this.context = context;
        this.elementInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getItemCount() {return elementData.size();}

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = elementInflater.inflate(R.layout.card_element, parent,false);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position){
        holder.bindData(elementData.get(position));
    }

    public void setItems(List<ListElement> items){elementData = items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgBreed;
        TextView txtNameBreed, txtOriginBreed, txtDescriptionBreed, txtTemperamentBreed;
        Button btnWiki;
        RatingBar rBarAdaptabilityBreed, rBarEnergyBreed, rBarIntelligenceBreed;
        ViewHolder(View itemView){
            super(itemView);
            imgBreed = itemView.findViewById(R.id.imgBreed);
            txtNameBreed = itemView.findViewById(R.id.txtNameBreed);
            txtOriginBreed = itemView.findViewById(R.id.txtOriginBreed);
            txtDescriptionBreed = itemView.findViewById(R.id.txtDescriptionBreed);
            txtTemperamentBreed = itemView.findViewById(R.id.txtTemperamentBreed);
            btnWiki = itemView.findViewById(R.id.btnWiki);
            rBarAdaptabilityBreed = itemView.findViewById(R.id.rBarAdaptabilityBreed);
            rBarEnergyBreed = itemView.findViewById(R.id.rBarEnergyBreed);
            rBarIntelligenceBreed = itemView.findViewById(R.id.rBarIntelligenceBreed);
        }
        void bindData(final ListElement item){
            if (item.getImgBreed() == null){ 
                imgBreed.setImageResource(R.drawable.ic_outline_insert_photo_24);
            }else {
                imgBreed.setImageBitmap(item.getImgBreed());
            }
            txtNameBreed.setText(item.getNameBreed());
            txtOriginBreed.setText(item.getOriginBreed());
            txtDescriptionBreed.setText(item.getTxtDescriptionBreed());
            txtTemperamentBreed.setText(item.getTemperamentBreed());
            rBarAdaptabilityBreed.setRating(item.getAdaptabilityBreed());
            rBarEnergyBreed.setRating(item.getEnergyBreed());
            rBarIntelligenceBreed.setRating(item.getIntelligenceBreed());
            btnWiki.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
