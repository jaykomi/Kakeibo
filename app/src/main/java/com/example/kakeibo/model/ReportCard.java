package com.example.kakeibo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportCard implements Parcelable {

    private String walletName, goalAchieved, improvement, howToImprove;

    public ReportCard(){

    }

    public ReportCard(String walletName) {
        this.walletName = walletName;
    }

    protected ReportCard(Parcel in) {
        walletName = in.readString();
        goalAchieved = in.readString();
        improvement = in.readString();
        howToImprove = in.readString();
    }

    public static final Creator<ReportCard> CREATOR = new Creator<ReportCard>() {
        @Override
        public ReportCard createFromParcel(Parcel in) {
            return new ReportCard(in);
        }

        @Override
        public ReportCard[] newArray(int size) {
            return new ReportCard[size];
        }
    };

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getGoalAchieved() {
        return goalAchieved;
    }

    public void setGoalAchieved(String goalAchieved) {
        this.goalAchieved = goalAchieved;
    }

    public String getImprovement() {
        return improvement;
    }

    public void setImprovement(String improvement) {
        this.improvement = improvement;
    }

    public String getHowToImprove() {
        return howToImprove;
    }

    public void setHowToImprove(String howToImprove) {
        this.howToImprove = howToImprove;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(walletName);
        parcel.writeString(goalAchieved);
        parcel.writeString(improvement);
        parcel.writeString(howToImprove);
    }

}
