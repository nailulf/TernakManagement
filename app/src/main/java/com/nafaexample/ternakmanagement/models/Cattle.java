package com.nafaexample.ternakmanagement.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Nailul on 4/14/2017.
 */
@IgnoreExtraProperties
public class Cattle {

    public String uid;
    public String id;
    public String spes;
    public String color;
    public String image;
    public long bw;
    public long bh;
    public long bc;
    public long age;
    public long timestamp;
    public long dayCount;
    public boolean medHistory;
    public int updateCount;

    public Cattle(){}

    public Cattle(String uid, String id, String spes, String color, String image,
                  long bw, long bh, long bc, long age, long dayCount, boolean medHistory, int updateCount) {

        long timestamp = new Date().getTime();
        this.uid = uid;
        this.id = id;
        this.spes = spes;
        this.color = color;
        this.image = image;
        this.bw = bw;
        this.bh = bh;
        this.bc = bc;
        this.age = age;
        this.timestamp = timestamp;
        this.dayCount = dayCount;
        this.medHistory = medHistory;
        this.updateCount = updateCount;
    }
    public Cattle(String color, String image, long bw, long bh, long bc, long age,
                  boolean medHistory) {
        long timestamp = new Date().getTime();

        this.color = color;
        this.image = image;
        this.bw = bw;
        this.bh = bh;
        this.bc = bc;
        this.age = age;
        this.timestamp = timestamp;
        this.dayCount = dayCount;
        this.medHistory = medHistory;

    }
    // [START post_to_init]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("id", id);
        result.put("bw", bw);
        result.put("bh", bh);
        result.put("bc", bc);
        result.put("age", age);
        result.put("spes", spes);
        result.put("color", color);
        result.put("dayCount", dayCount);
        result.put("image", image);
        result.put("timestamp", timestamp);
        result.put("updateCount", updateCount);
        result.put("medHistory", medHistory);

        return result;
    }
    // [END post_to_init]

    // [START post_to_update]
    @Exclude
    public Map<String, Object> toMapUp() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("bw", bw);
        result.put("bh", bh);
        result.put("bc", bc);
        result.put("age", age);
        result.put("color", color);
        result.put("dayCount", dayCount);
        result.put("image", image);
        result.put("timestamp", timestamp);
        result.put("medHistory", medHistory);

        return result;
    }
    // [END post_to_update]
}
