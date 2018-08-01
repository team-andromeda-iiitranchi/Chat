package chat.chat.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.List;

import chat.chat.R;

public class FieldAdapter extends RecyclerView.Adapter{
    private List mList;
    private RecyclerView recyclerView;
    ViewGroup parent;
    public FieldAdapter(List mList,RecyclerView recyclerView) {
        this.mList = mList;
        this.recyclerView=recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.edt_text_layout,parent,false);
        this.parent=parent;
        mList.remove(mList.size()-1);
        mList.add(view);
        return new FieldHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FieldHolder)holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    class FieldHolder extends RecyclerView.ViewHolder {
        EditText editText;
        View view;
        Button remButton;
        public FieldHolder(View itemView) {
            super(itemView);
            view=itemView;
            editText=(EditText)itemView.findViewById(R.id.edtField);
            remButton= (Button) itemView.findViewById(R.id.remButton);
        }
        public void bind(final int position)
        {
            remButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parent.removeView((View) view.getParent());
                    mList.remove(view.getParent());
                    notifyDataSetChanged();
                    recyclerView.scrollToPosition(mList.size()-1);
                }
            });
        }
    }
}
