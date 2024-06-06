package com.anyone.smardy.motaj.badtrew.fragments;

import static com.anyone.smardy.motaj.badtrew.Constants.Constants.DUBBED_ANIME;
import static com.anyone.smardy.motaj.badtrew.Constants.Constants.DUBBED_FILMS;
import static com.anyone.smardy.motaj.badtrew.Constants.Constants.FAVOURITE;
import static com.anyone.smardy.motaj.badtrew.Constants.Constants.MOST_VIEWED;
import static com.anyone.smardy.motaj.badtrew.Constants.Constants.NEW_ANIME;
import static com.anyone.smardy.motaj.badtrew.Constants.Constants.TRANSLATED_ANIME;
import static com.anyone.smardy.motaj.badtrew.Constants.Constants.TRANSLATED_FILMS;
import static com.anyone.smardy.motaj.badtrew.Constants.Constants.WATCHED;
import static com.anyone.smardy.motaj.badtrew.Constants.Constants.WATCH_LATER;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.activities.MainActivity;
import com.anyone.smardy.motaj.badtrew.adapters.CartoonsAdapter;
import com.anyone.smardy.motaj.badtrew.app.UserOptions;
import com.anyone.smardy.motaj.badtrew.databinding.FragmentCartoonBinding;
import com.anyone.smardy.motaj.badtrew.model.CartoonWithInfo;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CartoonFragment extends Fragment{


    FragmentCartoonBinding mBinding;

    int pageNumber = 1;
    List<CartoonWithInfo> cartoonList = new ArrayList<>();

    private final CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    boolean isOnRefresh = false;

    private CartoonsAdapter adapter;

    Fragment current ;

    int user_id ;
    int cartoon_type , classification ;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentCartoonBinding.inflate(inflater);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String viewVal = sharedPreferences.getString(getString(R.string.view_key), getString(R.string.grid));
        boolean grid = viewVal.equals(getString(R.string.grid));
        initRecyclerview(grid , true);
        initSwipeRefreshLayout();
        initRetrofit();
        return mBinding.getRoot();
    }

    public void initRecyclerview(boolean isGrid , boolean first_time) {
        mBinding.cartoonsRecyclerview.setHasFixedSize(true);
        adapter = new CartoonsAdapter(getActivity() , isGrid , false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        if (isGrid) {
            mBinding.cartoonsRecyclerview.setLayoutManager(gridLayoutManager);
        }
        else {
            mBinding.cartoonsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mBinding.cartoonsRecyclerview.setHasFixedSize(true);
        mBinding.cartoonsRecyclerview.setAdapter(adapter);
        if (!first_time) adapter.updateList(cartoonList);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        current = this ;
        LoginUtil loginUtil = new LoginUtil(getActivity()) ;
        user_id = loginUtil.getCurrentUser() != null ? loginUtil.getCurrentUser().getId() : -1 ;
        checkCartoonType(MainActivity.selectedType);
    }


    private void initRetrofit(){
        apiService = ApiClient.getClient(getActivity()).create(ApiService.class);
    }

    private void initSwipeRefreshLayout(){
        mBinding.swipeRefreshLayout.setRefreshing(true);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (MainActivity.searchCase) {
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                mBinding.swipeRefreshLayout.setRefreshing(true);
                if(cartoonList != null){
                    cartoonList.clear();
                    isOnRefresh = true;
                    pageNumber = 1;
                    checkCartoonType(MainActivity.selectedType);
                }
            }
        });
        mBinding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.selectedType==FAVOURITE) {
            if(cartoonList!=null) cartoonList.clear();
            getFavouriteCartoons();
        }
        else if (MainActivity.selectedType==WATCH_LATER) {
            if(cartoonList!=null) cartoonList.clear();
            getWatchLaterCartoons();
        }
        else if (MainActivity.selectedType==WATCHED) {
            if(cartoonList!=null) cartoonList.clear();
            getWatchedCartoons();
        }
    }

    public void getFavouriteCartoons() {
        List<CartoonWithInfo> retrievedCartoonList = new ArrayList<>(UserOptions.getUserOptions().getFavouriteCartoons());
        cartoonList.addAll(retrievedCartoonList);
        adapter.updateList(cartoonList);
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    public void getWatchedCartoons(){
        List<CartoonWithInfo> retrievedCartoonList = new ArrayList<>(UserOptions.getUserOptions().getWatchedCartoons());
        cartoonList.addAll(retrievedCartoonList);
        adapter.updateList(cartoonList);
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    public void getWatchLaterCartoons(){
        List<CartoonWithInfo> retrievedCartoonList = new ArrayList<>(UserOptions.getUserOptions().getWatchLaterCartoons());
        cartoonList.addAll(retrievedCartoonList);
        adapter.updateList(cartoonList);
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    public void getMostViewedCartoons(){
        mBinding.swipeRefreshLayout.setRefreshing(true);
        disposable.add(
                apiService
                        .getMostViewedCartoons()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                //Check if on refresh case
                                if (isOnRefresh) {
                                    cartoonList.clear();
                                    isOnRefresh = false;
                                }
                                cartoonList.addAll(retrievedCartoonList);
                                adapter.updateList(cartoonList);
                                mBinding.swipeRefreshLayout.setRefreshing(false);
                            }
                            @Override
                            public void onError(Throwable e) {
                                onGetCartoonError();
                            }
                        })
        );
    }

    public void filterData(String search) {
        mBinding.progressBarLayout.setVisibility(View.VISIBLE);
        // add api call to search with the given text
        disposable.add(
                apiService
                        .searchCartoon(search , cartoon_type , classification)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                adapter.updateList(retrievedCartoonList);
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError(Throwable e) {
                                resetDataAfterSearch();
                                Toast.makeText(getActivity(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }


    public void checkCartoonType(int categoryId){
        cartoonList.clear();
        pageNumber = 1;
        switch (categoryId){
            case DUBBED_ANIME: //All Cartoons
                cartoon_type = Constants.IS_SERIES ;
                classification = Constants.IS_DUBBED ;
                getCartoonsByType(DUBBED_ANIME);
                break;

            case DUBBED_FILMS: //Action Cartoons
                cartoon_type = Constants.IS_FILM ;
                classification = Constants.IS_DUBBED ;
                getCartoonsByType(DUBBED_FILMS);
                break;

            case TRANSLATED_ANIME:
                cartoon_type = Constants.IS_SERIES ;
                classification = Constants.IS_TRANSLATED ;
                getCartoonsByType(TRANSLATED_ANIME);
                break;

            case TRANSLATED_FILMS:
                cartoon_type = Constants.IS_FILM ;
                classification = Constants.IS_TRANSLATED ;
                getCartoonsByType(TRANSLATED_FILMS);
                break;

            case NEW_ANIME:
                getCartoonsByType(NEW_ANIME);
                break;

            case FAVOURITE:
                getFavouriteCartoons();
                break;

            case WATCH_LATER:
                getWatchLaterCartoons();
                break;

            case WATCHED:
                getWatchedCartoons();
                break;

            case MOST_VIEWED:
                getMostViewedCartoons();
                break;

            default: //No Action
        }
    }


    private void onGetCartoonError() {
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    private void updateUI(List<CartoonWithInfo> retrievedCartoonList) {
        mBinding.swipeRefreshLayout.setRefreshing(false);
        if (isOnRefresh) {
            cartoonList.clear();
            isOnRefresh = false;
        }
        cartoonList.addAll(retrievedCartoonList);
        adapter.updateList(cartoonList);
        pageNumber++;
    }

    //--------Override Methods------//

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    public CartoonsAdapter getAdapter () {
        return adapter ;
    }


    public RecyclerView getRecycleView () {
        return mBinding.cartoonsRecyclerview ;
    }

    public void getCartoonsByType(int selectedType) {
        if (selectedType == DUBBED_ANIME) {
            disposable.add(
                    apiService
                            .readPagingDUBBEDSeriesAnime(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }
        else if (selectedType == DUBBED_FILMS) {
            disposable.add(
                    apiService
                            .readPagingDUBBEDFilms(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }
        else if (selectedType == TRANSLATED_ANIME) {
            disposable.add(
                    apiService
                            .readPagingTranslatedSeriesAnime(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }
        else if (selectedType == TRANSLATED_FILMS) {
            disposable.add(
                    apiService
                            .readPagingTranslatedFilms(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }
        else if (selectedType == NEW_ANIME) {
            disposable.add(
                    apiService
                            .readPagingContinueAnime(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }

    }

    public void resetDataAfterSearch() {
        adapter.updateList(cartoonList);
    }
}
