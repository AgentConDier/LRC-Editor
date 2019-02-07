package com.cg.lrceditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HomePageListAdapter extends RecyclerView.Adapter<HomePageListAdapter.item> {
    public LinkedList<File> mFileList;
    private ArrayList<LyricsStatus> mStatusList;
    private LayoutInflater mInflator;

    private LyricFileSelectListener mClickListener;

    private SparseBooleanArray selectedItems;

    public HomePageListAdapter(Context context, LinkedList<File> fileList) {
        mStatusList = map_status(fileList);
        mInflator = LayoutInflater.from(context);
        this.mFileList = fileList;
        selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public HomePageListAdapter.item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflator.inflate(R.layout.lyricfile_item, parent, false);
        return new item(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePageListAdapter.item holder, int position) {
        String mCurrent = mFileList.get(position).getName();
        holder.songName.setText(mCurrent);

        holder.itemView.setActivated(selectedItems.get(position, false));
        mStatusList.get(position).applyTo(holder);

        applyClickEvents(holder, position);
    }

    private void applyClickEvents(final HomePageListAdapter.item holder, final int position) {
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectionCount() == 0)
                    mClickListener.fileSelected(mFileList.get(holder.getLayoutPosition()));
                else
                    mClickListener.onLyricItemClicked(position);
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onLyricItemSelected(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    void setClickListener(LyricFileSelectListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }

        notifyItemChanged(pos);
    }

    public void selectAll() {
        for (int i = 0; i < getItemCount(); i++)
            selectedItems.put(i, true);
        notifyDataSetChanged();
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public int getSelectionCount() {
        return selectedItems.size();
    }

    public interface LyricFileSelectListener {
        void fileSelected(File fileName);

        void onLyricItemSelected(int position);

        void onLyricItemClicked(int position);
    }

    class item extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final HomePageListAdapter mAdapter;
        private final LinearLayout linearLayout;
        private final TextView songName;
        private final TextView statusText;

        public item(View itemView, HomePageListAdapter adapter) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.lyric_file_parent_linearlayout);
            songName = itemView.findViewById(R.id.song_textview);
            statusText = itemView.findViewById(R.id.status_textview);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getSelectionCount() == 0) {
                File song_name = mFileList.get(getLayoutPosition());
                if (mClickListener != null) mClickListener.fileSelected(song_name);
            } else {
                mClickListener.onLyricItemClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mClickListener.onLyricItemSelected(getAdapterPosition());
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return false;
        }

    }

    public enum LyricsStatus {
        BOTH, LRC, AUDIO, UNDEFINED;

        void applyTo(item holder) {
            switch(this) {
                case BOTH:
                    /*
                      R.drawable.bg_list_row is already standard, but if it is not set here
                      it can sometimes be changed to the manually set background of another element
                      by selecting multiple elements (Bug?)
                    */
                    holder.itemView.setBackgroundResource(R.drawable.bg_list_row);
                    holder.statusText.setText(R.string.files_both);
                    break;
                case LRC:
                    holder.itemView.setBackgroundResource(R.drawable.bg_list_row_noaudio);
                    holder.statusText.setText(R.string.files_lrc);
                    break;
                case AUDIO:
                    holder.itemView.setBackgroundResource(R.drawable.bg_list_row_nolrc);
                    holder.statusText.setText(R.string.files_audio);
                    break;
                default:
                    Log.w("ApplyLyricsStatus", "The status of "+holder.songName.getText()+" was undefined");
                    break;
            }
        }
    }

    private ArrayList<LyricsStatus> map_status(LinkedList<File> files) {
        Iterator<File> i = files.iterator();
        ArrayList<LyricsStatus> out = new ArrayList<>();

        while (i.hasNext()) {
            File f = i.next();
            LyricsStatus s = LyricsStatus.UNDEFINED;
            String name = f.getAbsolutePath();
            /*
              extension can be removed by substring(-4) because nam only gets used if the
              extension is .lrc, .mp3 or .m4a ergo 4 characters long
            */
            String nam = name.substring(0,name.length()-4);
            if (name.endsWith(".lrc")) {
                s = LyricsStatus.LRC;
                if (files.contains(new File(nam+".mp3")) ||
                    files.contains(new File(nam+".m4a"))) {
                    s = LyricsStatus.BOTH;
                }
            } else if (name.endsWith(".mp3") ||
                       name.endsWith(".m4a")) {
                if (files.contains(new File(nam+".lrc"))) {
                    i.remove();
                    continue; // skips out.add(s)
                }
                s = LyricsStatus.AUDIO;
            }
            out.add(s);
        }
        return out;
    }
    public void map_status() {
        mStatusList = map_status(mFileList);
    }
}