package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.Recipe;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    // =========================================================
    // DATA
    // =========================================================

    private final List<Recipe> recipes;

    private final OnRecipeClickListener listener;


    // =========================================================
    // CLICK LISTENER
    // =========================================================

    public interface OnRecipeClickListener {

        void onRecipeClick(Recipe recipe);

    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RecipeAdapter(
            List<Recipe> recipes,
            OnRecipeClickListener listener) {

        this.recipes = recipes;

        this.listener = listener;
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_recipe,
                parent,
                false
        );

        return new RecipeViewHolder(view);
    }


    // =========================================================
    // BIND DATA
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position) {

        Recipe recipe = recipes.get(position);


        // -----------------------------------------------------
        // Recipe name
        // -----------------------------------------------------

        holder.tvRecipeName.setText(
                recipe.getName()
        );


        // -----------------------------------------------------
        // View Recipe button
        // -----------------------------------------------------

        holder.btnViewRecipe.setOnClickListener(view -> {

            if (listener != null) {

                listener.onRecipeClick(recipe);
            }
        });


        // -----------------------------------------------------
        // Entire card clickable
        // -----------------------------------------------------

        holder.itemView.setOnClickListener(view -> {

            if (listener != null) {

                listener.onRecipeClick(recipe);
            }
        });
    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return recipes.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRecipeName;

        Button btnViewRecipe;


        public RecipeViewHolder(
                @NonNull View itemView) {

            super(itemView);


            tvRecipeName =
                    itemView.findViewById(
                            R.id.tvRecipeName
                    );


            btnViewRecipe =
                    itemView.findViewById(
                            R.id.btnViewRecipe
                    );
        }
    }
}