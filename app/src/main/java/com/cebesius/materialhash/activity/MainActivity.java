package com.cebesius.materialhash.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cebesius.materialhash.MaterialHashApp;
import com.cebesius.materialhash.R;
import com.cebesius.materialhash.domain.boundary.FileBoundary;
import com.cebesius.materialhash.domain.boundary.HashOperationBoundary;
import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.domain.entity.HashOperation;
import com.cebesius.materialhash.domain.interactor.HashOperationInteractor;
import com.cebesius.materialhash.fragment.ObjectRetentionFragment;
import com.cebesius.materialhash.fragment.SingleChoiceDialogFragment;
import com.cebesius.materialhash.mvp.HashOperationModelImpl;
import com.cebesius.materialhash.mvp.HashOperationPresenter;
import com.cebesius.materialhash.mvp.HashOperationView;
import com.cebesius.materialhash.util.BusSingleton;
import com.cebesius.materialhash.util.HashAlgorithmsGateway;
import com.cebesius.materialhash.util.rx.AppRxSchedulers;
import com.cebesius.materialhash.util.rx.RxSchedulers;
import com.google.common.base.Optional;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class MainActivity extends AppCompatActivity implements HashOperationView {

    private static final int REQUEST_CODE_PICK_INPUT_FILE = 0;
    private static final String FRAGMENT_TAG_ALGORITHM_CHOICE = "algorithmChoice";
    private static final String FRAGMENT_TAG_OBJECT_RETENTION = "objectRetention";
    private static final String RETENTION_KEY_HASH_OPERATIONS_MVP_MODEL = "hashOperationsMvp.model";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.scroll_view)
    ScrollView scrollView;
    @InjectView(R.id.file_card)
    CardView fileCard;
    @InjectView(R.id.file_input)
    TextView fileInput;
    @InjectView(R.id.file_input_size)
    TextView fileInputSize;
    @InjectView(R.id.algorithms_card)
    CardView algorithmsCard;
    @InjectView(R.id.algorithms_input)
    TextView algorithmsInput;
    @InjectView(R.id.operation_card)
    CardView operationCard;
    @InjectView(R.id.operation_start_button)
    Button operationStartButton;
    @InjectView(R.id.operation_progress)
    ProgressBar operationProgress;
    @InjectView(R.id.operation_result)
    TextView operationResult;

    private RxSchedulers rxSchedulers;
    private Bus bus;
    private HashAlgorithmsMvpTriad hashOperationsMvp;
    private WeakReference<ObjectRetentionFragment> objectRetentionFragmentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initObjectRetentionFragment();
        initBus();
        initToolbar();
        initHashAlgorithmsMvp();
    }

    private void initObjectRetentionFragment() {
        if (objectRetentionFragmentRef != null && objectRetentionFragmentRef.get() != null) {
            return;
        }
        ObjectRetentionFragment objectRetentionFragment = (ObjectRetentionFragment) getSupportFragmentManager()
            .findFragmentByTag(FRAGMENT_TAG_OBJECT_RETENTION);
        if (objectRetentionFragment == null) {
            objectRetentionFragment = new ObjectRetentionFragment();
            getSupportFragmentManager().beginTransaction()
                .add(objectRetentionFragment, FRAGMENT_TAG_OBJECT_RETENTION)
                .commit();
        }
        objectRetentionFragmentRef = new WeakReference<>(objectRetentionFragment);
    }

    private ObjectRetentionFragment getObjectRetentionFragment() {
        return objectRetentionFragmentRef.get();
    }

    private RxSchedulers getRxSchedulers() {
        if (rxSchedulers == null) {
            rxSchedulers = MaterialHashApp.getRxSchedulers();
        }
        return rxSchedulers;
    }

    private void initBus() {
        bus = BusSingleton.getInstance();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initHashAlgorithmsMvp() {
        hashOperationsMvp = new HashAlgorithmsMvpTriad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        hashOperationsMvp.presenter.start();
        hashOperationsMvp.model.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hashOperationsMvp.presenter.stop();
        hashOperationsMvp.model.stop();
        bus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        hashOperationsMvp.model.saveState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PICK_INPUT_FILE:
                onInputFilePicked(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showAvailableHashAlgorithms(List<HashAlgorithm> hashAlgorithms) {
        algorithmsCard.setVisibility(View.VISIBLE);
        algorithmsInput.requestFocus(View.FOCUS_DOWN);
    }

    @Override
    public void revealAvailableHashAlgorithms(List<HashAlgorithm> hashAlgorithms) {
        showAvailableHashAlgorithms(hashAlgorithms);
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

    @Override
    public void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm) {
        algorithmsInput.setText(hashAlgorithm.getLabel());
    }

    @Subscribe
    public void onSingleChoiceDialogFragmentChoiceEvent(SingleChoiceDialogFragment.ChoiceEvent event) {
        switch (event.getTag()) {
            case FRAGMENT_TAG_ALGORITHM_CHOICE:
                onUserSelectedOperationHashAlgorithm(event.getCheckedItem());
                break;
        }
    }

    private void onUserSelectedOperationHashAlgorithm(int checkedItem) {
        List<HashAlgorithm> availableHashAlgorithms = hashOperationsMvp.model.getAvailableHashAlgorithms().get();
        HashAlgorithm hashAlgorithm = availableHashAlgorithms.get(checkedItem);
        hashOperationsMvp.presenter.onUserSelectedOperationHashAlgorithm(hashAlgorithm);
    }

    @OnClick(R.id.file_input)
    public void onFileInputClicked() {
        hashOperationsMvp.presenter.onUserRequestingShowOperationFilePicker();
    }

    @Override
    public void showOperationFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_INPUT_FILE);
    }

    private void onInputFilePicked(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        FileBoundary fileBoundary = new FileBoundary(getContentResolver());
        File file = fileBoundary.toEntity(data);
        hashOperationsMvp.presenter.onUserSelectedOperationFile(file);
    }

    @Override
    public void setOperationFile(File file) {
        fileInput.setText(file.getDisplayName());
        fileInputSize.setText(Formatter.formatShortFileSize(this, file.getSize()));
    }

    @OnClick(R.id.algorithms_input)
    public void onAlgorithmsInputClicked() {
        hashOperationsMvp.presenter.onUserRequestingShowOperationHashAlgorithmPicker();
    }

    @Override
    public void showOperationHashAlgorithmPicker(
        List<HashAlgorithm> availableHashAlgorithms,
        Optional<HashAlgorithm> operationHashAlgorithmContainer
    ) {
        Observable<SingleChoiceDialogFragment> observable = Observable.create(
            subscriber -> {
                CharSequence[] items = new CharSequence[availableHashAlgorithms.size()];
                int i = 0;
                for (HashAlgorithm availableHashAlgorithm : availableHashAlgorithms) {
                    items[i++] = availableHashAlgorithm.getLabel();
                }
                int checkedItem;
                if (operationHashAlgorithmContainer.isPresent()) {
                    checkedItem = availableHashAlgorithms.indexOf(operationHashAlgorithmContainer.get());
                } else {
                    checkedItem = SingleChoiceDialogFragment.CHECKED_ITEM_NONE;
                }
                SingleChoiceDialogFragment fragment = SingleChoiceDialogFragment.newInstance(
                    items,
                    checkedItem
                );
                subscriber.onNext(fragment);
            }
        );
        observable.subscribeOn(getRxSchedulers().computationThread())
            .observeOn(getRxSchedulers().mainThread())
            .subscribe(fragment -> fragment.show(getSupportFragmentManager(), FRAGMENT_TAG_ALGORITHM_CHOICE));
    }

    @Override
    public void revealHashOperationStart() {
        showHashOperationStart();
        long animationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator(this, null);
        ObjectAnimator animateTranslationY = ObjectAnimator.ofFloat(
            operationCard,
            "translationY",
            scrollView.getHeight(),
            0
        );
        animateTranslationY.setDuration(animationDuration)
            .setInterpolator(interpolator);
        ObjectAnimator animateRotation = ObjectAnimator.ofFloat(
            operationCard,
            "rotation",
            180,
            0
        );
        animateRotation.setDuration(animationDuration)
            .setInterpolator(interpolator);
        animateTranslationY.start();
        animateRotation.start();
    }

    @Override
    public void showHashOperationStart() {
        operationCard.setVisibility(View.VISIBLE);
        operationStartButton.requestFocus(View.FOCUS_DOWN);
    }

    @OnClick(R.id.operation_start_button)
    public void onOperationStartButtonClicked() {
        hashOperationsMvp.presenter.onUserRequestingStartHashOperation();
    }

    @Override
    public void updateHashOperationProgress(HashOperation hashOperation) {
        if (operationStartButton.getVisibility() != View.GONE) {
            operationStartButton.setVisibility(View.GONE);
            operationProgress.setVisibility(View.VISIBLE);
            operationProgress.setProgress(0);
            fileInput.setEnabled(false);
            algorithmsInput.setEnabled(false);
        }
        int progressBarMax = operationProgress.getMax();
        double operationProgress = hashOperation.getProgress();
        int displayProgress = Math.round((float) operationProgress * (float) progressBarMax);
        this.operationProgress.setProgress(displayProgress);
    }

    @Override
    public void showHashOperationResult(HashOperation hashOperation) {
        operationProgress.setVisibility(View.GONE);
        operationStartButton.setVisibility(View.GONE);
        operationResult.setText(hashOperation.getHash());
    }

    @Override
    public void showHashOperationError() {
        operationProgress.setVisibility(View.GONE);
        operationResult.setText("oops, something went wrong");

    }

    private class HashAlgorithmsMvpTriad {

        private final HashOperationModelImpl model;
        private final HashOperationView view;
        private final HashOperationPresenter presenter;

        private HashAlgorithmsMvpTriad() {
            HashOperationInteractor hashOperationInteractor = new HashOperationInteractor(
                new HashOperationBoundary(getContentResolver())
            );
            HashOperationModelImpl model;
            if (getObjectRetentionFragment().containsKey(RETENTION_KEY_HASH_OPERATIONS_MVP_MODEL)) {
                model = getObjectRetentionFragment().get(RETENTION_KEY_HASH_OPERATIONS_MVP_MODEL);
            } else {
                model = new HashOperationModelImpl(
                    getRxSchedulers(),
                    new HashAlgorithmsGateway()
                );
                getObjectRetentionFragment().put(RETENTION_KEY_HASH_OPERATIONS_MVP_MODEL, model);
            }
            this.model = model;
            this.view = MainActivity.this;
            this.presenter = new HashOperationPresenter(
                model,
                view,
                hashOperationInteractor,
                getRxSchedulers()
            );
        }
    }
}
