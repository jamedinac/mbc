package Common;

import java.util.ArrayList;

public class GeneProfile<T>{

    private final ArrayList<T> profileData;

    public GeneProfile(ArrayList<T> profileData){
        this.profileData = profileData;
    }

    public GeneProfile(int size, T value) {
        this.profileData = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            profileData.set(i, value);
        }
    }

    public ArrayList<T> getProfileData(){
        return profileData;
    }

    public int getSize(){
        return profileData.size();
    }

    public T get(int index){
        return profileData.get(index);
    }
}
