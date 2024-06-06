package com.anyone.smardy.motaj.badtrew.app;

import com.anyone.smardy.motaj.badtrew.model.CartoonWithInfo;

import java.util.ArrayList;
import java.util.List;

final public class UserOptions {

    private static UserOptions userOptions ;
    private List<CartoonWithInfo> favouriteCartoons ;
    private List<CartoonWithInfo> watchedCartoons ;
    private List<CartoonWithInfo> watchLaterCartoons ;
    private List<Integer> favouriteCartoonsIds ;
    private List<Integer> watchedCartoonsIds ;
    private List<Integer> watchLaterCartoonsIds ;
    private List<Integer> seenEpisodesIds ;

    private UserOptions () {
        favouriteCartoons = new ArrayList<>();
        watchedCartoons = new ArrayList<>();
        watchLaterCartoons = new ArrayList<>();
        favouriteCartoonsIds = new ArrayList<>();
        watchedCartoonsIds = new ArrayList<>();
        watchLaterCartoonsIds = new ArrayList<>();
        seenEpisodesIds = new ArrayList<>() ;
    }

    public static UserOptions getUserOptions() {
        if (userOptions == null) {
            userOptions = new UserOptions();
        }
        return userOptions ;
    }

    public List<CartoonWithInfo> getFavouriteCartoons() {
        return favouriteCartoons;
    }

    public void setFavouriteCartoons(List<CartoonWithInfo> favouriteCartoons) {
        this.favouriteCartoons = favouriteCartoons;
        updateFavouriteCartoonsIds();
    }

    private void updateFavouriteCartoonsIds() {
        favouriteCartoonsIds.clear();
        for (CartoonWithInfo favouriteCartoon : favouriteCartoons) {
            favouriteCartoonsIds.add(favouriteCartoon.getId());
        }
    }

    private void updateWatchedCartoonsIds() {
        watchedCartoonsIds.clear();
        for (CartoonWithInfo watchedCartoon : watchedCartoons) {
            watchedCartoonsIds.add(watchedCartoon.getId());
        }
    }

    private void updateWatchLaterCartoonsIds() {
        watchLaterCartoonsIds.clear();
        for (CartoonWithInfo watchLaterCartoon : watchLaterCartoons) {
            watchLaterCartoonsIds.add(watchLaterCartoon.getId());
        }
    }

    public List<CartoonWithInfo> getWatchedCartoons() {
        return watchedCartoons;
    }

    public void setWatchedCartoons(List<CartoonWithInfo> watchedCartoons) {
        this.watchedCartoons = watchedCartoons;
        updateWatchedCartoonsIds();
    }

    public List<CartoonWithInfo> getWatchLaterCartoons() {
        return watchLaterCartoons;
    }

    public void setWatchLaterCartoons(List<CartoonWithInfo> watchLaterCartoons) {
        this.watchLaterCartoons = watchLaterCartoons;
        updateWatchLaterCartoonsIds();
    }

    public List<Integer> getFavouriteCartoonsIds() {
        return favouriteCartoonsIds;
    }

    public void setFavouriteCartoonsIds(List<Integer> favouriteCartoonsIds) {
        this.favouriteCartoonsIds = favouriteCartoonsIds;
    }

    public List<Integer> getWatchedCartoonsIds() {
        return watchedCartoonsIds;
    }

    public void setWatchedCartoonsIds(List<Integer> watchedCartoonsIds) {
        this.watchedCartoonsIds = watchedCartoonsIds;
    }

    public List<Integer> getWatchLaterCartoonsIds() {
        return watchLaterCartoonsIds;
    }

    public void setWatchLaterCartoonsIds(List<Integer> watchLaterCartoonsIds) {
        this.watchLaterCartoonsIds = watchLaterCartoonsIds;
    }

    public List<Integer> getSeenEpisodesIds() {
        return seenEpisodesIds;
    }

    public void setSeenEpisodesIds(List<Integer> seenEpisodesIds) {
        this.seenEpisodesIds = seenEpisodesIds;
    }
}
