package com.example.brainquiz.filter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tingkatan {
    @SerializedName("ID")
    private String id; // Included for deserialization from GET responses, excluded from POST

    @SerializedName("name")
    @Expose
    private String name; // Included in POST request body

    @SerializedName("description")
    @Expose
    private String description; // Included in POST request body

    // Default constructor
    public Tingkatan() {
        this.id = "";
        this.name = "";
        this.description = "";
    }

    // Getter and Setter for id
    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for name (aliased as nama for compatibility)
    public String getNama() {
        return name != null ? name : "";
    }

    public void setNama(String name) {
        this.name = name;
    }

    // Getter and Setter for description
    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }
}