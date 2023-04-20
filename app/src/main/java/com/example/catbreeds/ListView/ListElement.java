package com.example.catbreeds.ListView;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ListElement implements Serializable {
    public String nameBreed;
    public String originBreed;
    public String txtDescriptionBreed;
    public String urlBreed;
    public String temperamentBreed;
    public int adaptabilityBreed;
    public int energyBreed;
    public int intelligenceBreed;
    public Bitmap imgBreed;

    public ListElement(String nameBreed,
                       String originBreed,
                       String txtDescriptionBreed,
                       String urlBreed,
                       String temperamentBreed,
                       Bitmap imgBreed,
                       int adaptabilityBreed,
                       int energyBreed,
                       int intelligenceBreed) {
        this.nameBreed = nameBreed;
        this.originBreed = originBreed;
        this.txtDescriptionBreed = txtDescriptionBreed;
        this.urlBreed = urlBreed;
        this.temperamentBreed = temperamentBreed;
        this.imgBreed = imgBreed;
        this.adaptabilityBreed = adaptabilityBreed;
        this.energyBreed = energyBreed;
        this.intelligenceBreed = intelligenceBreed;
    }

    public int getAdaptabilityBreed() {
        return adaptabilityBreed;
    }

    public void setAdaptabilityBreed(int adaptabilityBreed) {
        this.adaptabilityBreed = adaptabilityBreed;
    }

    public int getEnergyBreed() {
        return energyBreed;
    }

    public void setEnergyBreed(int energyBreed) {
        this.energyBreed = energyBreed;
    }

    public int getIntelligenceBreed() {
        return intelligenceBreed;
    }

    public void setIntelligenceBreed(int intelligenceBreed) {
        this.intelligenceBreed = intelligenceBreed;
    }

    public String getTemperamentBreed() {
        return temperamentBreed;
    }

    public void setTemperamentBreed(String temperamentBreed) {
        this.temperamentBreed = temperamentBreed;
    }

    public String getUrlBreed() {
        return urlBreed;
    }

    public void setUrlBreed(String urlBreed) {
        this.urlBreed = urlBreed;
    }

    public String getNameBreed() {
        return nameBreed;
    }

    public void setNameBreed(String nameBreed) {
        this.nameBreed = nameBreed;
    }

    public String getOriginBreed() {
        return originBreed;
    }

    public void setOriginBreed(String originBreed) {
        this.originBreed = originBreed;
    }

    public String getTxtDescriptionBreed() {
        return txtDescriptionBreed;
    }

    public void setTxtDescriptionBreed(String txtDescriptionBreed) {
        this.txtDescriptionBreed = txtDescriptionBreed;
    }

    public Bitmap getImgBreed() {
        return imgBreed;
    }

    public void setImgBreed(Bitmap imgBreed) {
        this.imgBreed = imgBreed;
    }
}
