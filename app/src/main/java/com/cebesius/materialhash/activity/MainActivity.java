package com.cebesius.materialhash.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cebesius.materialhash.R;
import com.cebesius.materialhash.domain.HashAlgorithm;
import com.cebesius.materialhash.mvp.HashAlgorithmsModelImpl;
import com.cebesius.materialhash.mvp.HashAlgorithmsPresenter;
import com.cebesius.materialhash.mvp.HashAlgorithmsView;
import com.cebesius.materialhash.util.HashAlgorithmsGateway;
import com.cebesius.materialhash.util.rx.AppRxSchedulers;
import com.cebesius.materialhash.util.rx.RxSchedulers;
import com.google.common.collect.ImmutableList;
import com.nineoldandroids.animation.AnimatorSet;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.ganfra.materialspinner.MaterialSpinner;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements HashAlgorithmsView {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.scroll_view)
    ScrollView scrollView;
    @InjectView(R.id.algorithms_card)
    CardView algorithmsCard;
    @InjectView(R.id.algorithms_card_spinner)
    MaterialSpinner algorithmsSpinner;

    private HashAlgorithmsMvpTriad hashAlgorithmsMvp;
    private ArrayAdapter<HashAlgorithm> hashAlgorithmsSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initToolbar();
        initHashAlgorithmsMvp(savedInstanceState);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initHashAlgorithmsMvp(Bundle savedInstanceState) {
        hashAlgorithmsMvp = new HashAlgorithmsMvpTriad();
        if (savedInstanceState != null) {
            hashAlgorithmsMvp.model.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hashAlgorithmsMvp.presenter.start();
        hashAlgorithmsMvp.model.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hashAlgorithmsMvp.presenter.stop();
        hashAlgorithmsMvp.model.stop();
    }

    @Override
    public void showAvailableHashAlgorithms(List<HashAlgorithm> hashAlgorithms) {
        hashAlgorithmsSpinnerAdapter = new ArrayAdapter<>(
            MainActivity.this,
            android.R.layout.simple_spinner_item,
            hashAlgorithms
        );
        hashAlgorithmsSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        algorithmsSpinner.setAdapter(hashAlgorithmsSpinnerAdapter);
        algorithmsSpinner.setOnItemSelectedListener(algorithmsSpinnerItemSelectedListener);
        algorithmsCard.setVisibility(View.VISIBLE);
        long animationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator(this, null);
        ObjectAnimator animateTranslationY = ObjectAnimator.ofFloat(
            algorithmsCard,
            "translationY",
            scrollView.getHeight(),
            0
        );
        animateTranslationY.setDuration(animationDuration)
            .setInterpolator(interpolator);
        ObjectAnimator animateRotation = ObjectAnimator.ofFloat(
            algorithmsCard,
            "rotation",
            180,
            0
        );
        animateRotation.setDuration(animationDuration)
            .setInterpolator(interpolator);
        animateTranslationY.start();
        animateRotation.start();
    }

    private final AdapterView.OnItemSelectedListener algorithmsSpinnerItemSelectedListener =
        new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position < 0) {
                return;
            }
            HashAlgorithm hashAlgorithm = hashAlgorithmsSpinnerAdapter.getItem(position);
            hashAlgorithmsMvp.presenter.onUserSelectedHashAlgorithm(hashAlgorithm);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            hashAlgorithmsMvp.presenter.onUserSelectedHashAlgorithm(null);
        }
    };

    private class HashAlgorithmsMvpTriad {

        private final HashAlgorithmsModelImpl model;
        private final HashAlgorithmsView view;
        private final HashAlgorithmsPresenter presenter;

        private HashAlgorithmsMvpTriad() {
            RxSchedulers rxSchedulers = new AppRxSchedulers();
            this.model = new HashAlgorithmsModelImpl(rxSchedulers, new HashAlgorithmsGateway());
            this.view = MainActivity.this;
            this.presenter = new HashAlgorithmsPresenter(model, view, rxSchedulers);
        }
    }
}
