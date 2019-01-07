package com.nastia.cf;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sysdata.widget.accordion.ExpandableItemHolder;
import com.sysdata.widget.accordion.FancyAccordionView;
import com.sysdata.widget.accordion.Item;
import com.sysdata.widget.accordion.ItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class questionsActivity extends AppCompatActivity {

    private static final String KEY_EXPANDED_ID = "expandedId";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = signUpActivity.class.getSimpleName();

    Button backBtn;
    private FancyAccordionView mRecyclerView;
    private ItemAdapter.OnItemClickedListener mListener = new ItemAdapter.OnItemClickedListener() {
        @Override
        public void onItemClicked(ItemAdapter.ItemViewHolder<?> viewHolder, int id) {
            ItemAdapter.ItemHolder itemHolder = viewHolder.getItemHolder();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);


        mRecyclerView = (FancyAccordionView) findViewById(R.id.alarms_recycler_view);

        // bind the factory to create view holder for item collapsed
        mRecyclerView.setCollapsedViewHolderFactory(SampleCollapsedViewHolder.Factory.create(R.layout.collapsed), mListener);

        // bind the factory to create view holder for item expanded
        mRecyclerView.setExpandedViewHolderFactory(SampleExpandedViewHolder.Factory.create(R.layout.expanded), mListener);

        // restore the expanded item from state
        if (savedInstanceState != null) {
            mRecyclerView.setExpandedItemId(savedInstanceState.getLong(KEY_EXPANDED_ID, Item.INVALID_ID));
        }

        backBtn=(Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadData();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_EXPANDED_ID, mRecyclerView.getExpandedItemId());
    }

    private void loadData() {
        final List<questionItem> q = new ArrayList<>();
        db.collection("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                questionItem newQ =new questionItem(document.getString("question"),
                                        document.getString("answer"));
                                q.add(newQ);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        final List<ExpandableItemHolder> itemHolders = new ArrayList<>();
                        Item itemModel;
                        ExpandableItemHolder itemHolder;
                        for ( questionItem item : q) {
            itemModel = questionItem.create(item.getTitle(), item.getDescription());
//                            itemModel = questionItem.create("vvvv", "rgregeg");
                            itemHolder = new ExpandableItemHolder(itemModel);
                            itemHolders.add(itemHolder);
                        }

                        mRecyclerView.setAdapterItems(itemHolders);
                    }
                });
        final int dataCount = 50;
        int index = 0;

    }

}
