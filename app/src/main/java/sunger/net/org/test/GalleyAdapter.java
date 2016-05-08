package sunger.net.org.test;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by sunger on 16/4/29.
 */
public class GalleyAdapter extends RecyclerView.Adapter<GalleyAdapter.ViewHolder> {
    private List<ImageEntity> data;


    public void setData(List<ImageEntity> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_view, parent,
                false));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageEntity entity = data.get(position);
        holder.mImageView.setImageResource(entity.getCover());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView_cover);
        }
    }
}
