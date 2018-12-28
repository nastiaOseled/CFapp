package com.nastia.cf;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class NutritionAdapter extends RecyclerView.Adapter<NutritionAdapter.ViewHolder>{

    private List<Food> mNutrition;

    public NutritionAdapter(List<Food> mNutrition) {
        this.mNutrition = mNutrition;
    }

    @NonNull
    @Override
    public NutritionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View nutritionView = inflater.inflate(R.layout.item_nutrition, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(nutritionView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NutritionAdapter.ViewHolder viewHolder, int i) {
        // Get the data model based on position
        Food food = mNutrition.get(i);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(food.getName()+" "+food.getUnit());
        textView = viewHolder.caloriesTextView;
        textView.setText(food.getCalories().toString());
       // Button button = viewHolder.messageButton;
      //  button.setEnabled(true);
    }

    @Override
    public int getItemCount() {
        return mNutrition.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView caloriesTextView;
   //    public Button messageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nutrition_name);
            caloriesTextView = (TextView) itemView.findViewById(R.id.nutrition_calories);
            //messageButton = (Button) itemView.findViewById(R.id.message_button);
        }
    }
}
