package com.example.kinderfind.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.kinderfind.R;
import com.example.kinderfind.entities.RatingReview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RatingReviewAdapter extends RecyclerView.Adapter<RatingReviewAdapter.RatingReviewHolder> {
    private List<RatingReview> ratingReviewsList = new ArrayList<>();
    private List<String> mkeys;
    Context context;
    private StorageReference mStorageRef;

    public RatingReviewAdapter(List<RatingReview> ratingReviews, List<String> mkeys, Context context) {
        this.ratingReviewsList = ratingReviews;
        this.mkeys = mkeys;
        this.context = context;
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pic");
    }

    @NonNull
    @Override
    public RatingReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new RatingReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RatingReviewHolder holder, int position) {
        PrettyTime dateTime = new PrettyTime();
        RatingReview currentRatingReview = ratingReviewsList.get(position);
        holder.username.setText(currentRatingReview.getUsername());
        holder.review.setText(currentRatingReview.getReview());
        holder.ratingBar.setRating((float)(currentRatingReview.getTotal_rating()));
        holder.date.setText(dateTime.format(new Date(currentRatingReview.getTimestamp())));

        mStorageRef.child(currentRatingReview.getUser_id()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context)
                                .load(uri)
                                .placeholder(R.mipmap.ic_launcher)
                                .apply(RequestOptions.circleCropTransform())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.image);
                        Log.d("Get Profile Pic", "Profile Pic Retrieval Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Get Profile Pic", "Profile Pic Retrieval Failed");
            }
        });

    }

    @Override
    public int getItemCount() {
        return ratingReviewsList.size();
    }

    public void setRatingReviewsList(List<RatingReview> ratingReviews){
        this.ratingReviewsList = ratingReviews;
        notifyDataSetChanged();
    }

    class RatingReviewHolder extends RecyclerView.ViewHolder{
        private TextView username, review, date;
        private ImageView image;
        private MaterialRatingBar ratingBar;

        public RatingReviewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.review_info_name);
            review = itemView.findViewById(R.id.review_info_review);
            date = itemView.findViewById(R.id.review_date);
            image = itemView.findViewById(R.id.review_image);
            ratingBar = itemView.findViewById(R.id.review_info_stars);

        }
    }

}
