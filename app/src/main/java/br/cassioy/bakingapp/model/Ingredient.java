
package br.cassioy.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ingredient implements Parcelable {

    @SerializedName("quantity")
    @Expose
    private Double quantity;
    @SerializedName("measure")
    @Expose
    private String measure;
    @SerializedName("ingredient")
    @Expose
    private String ingredient;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public static class Step implements Parcelable{

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("shortDescription")
        @Expose
        private String shortDescription;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("videoURL")
        @Expose
        private String videoURL;
        @SerializedName("thumbnailURL")
        @Expose
        private String thumbnailURL;


        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public void setVideoURL(String videoURL) {
            this.videoURL = videoURL;
        }

        public String getThumbnailURL() {
            return thumbnailURL;
        }

        public void setThumbnailURL(String thumbnailURL) {
            this.thumbnailURL = thumbnailURL;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.id);
            dest.writeString(this.shortDescription);
            dest.writeString(this.description);
            dest.writeString(this.videoURL);
            dest.writeString(this.thumbnailURL);
        }

        public Step() {
        }

        protected Step(Parcel in) {
            this.id = (Integer) in.readValue(Integer.class.getClassLoader());
            this.shortDescription = in.readString();
            this.description = in.readString();
            this.videoURL = in.readString();
            this.thumbnailURL = in.readString();
        }

        public static final Creator<Step> CREATOR = new Creator<Step>() {
            @Override
            public Step createFromParcel(Parcel source) {
                return new Step(source);
            }

            @Override
            public Step[] newArray(int size) {
                return new Step[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.quantity);
        dest.writeString(this.measure);
        dest.writeString(this.ingredient);
    }

    public Ingredient() {
    }

    protected Ingredient(Parcel in) {
        this.quantity = (Double) in.readValue(Double.class.getClassLoader());
        this.measure = in.readString();
        this.ingredient = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
