package seojihyun.odya.pineapple;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.protocol.NoticeData;
import seojihyun.odya.pineapple.protocol.Protocol;


/**
 * Created by Gordon Wong on 7/18/2015.
 *
 * Adapter for the all items screen.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

	private Context context;
	private ArrayList<Note> notes;
	DataManager dataManager;

	public NotesAdapter(Context context, int numNotes) {
		this.context = context;
		this.dataManager = (DataManager)context.getApplicationContext(); //서지현
		//notes = generateNotes(context);
	}

	public void delete(int position) {
		notes.remove(position);
//		notes.notify();
		//notifyItemRemoved(position);
		notifyDataSetChanged();

	}

	@Override
	public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_note, parent,
				false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		Note noteModel = notes.get(position);
		String title = noteModel.getTitle();
		String note = noteModel.getNote();
		String info = noteModel.getInfo();
		int infoImage = noteModel.getInfoImage();
		int color = noteModel.getColor();

		// Set text
		holder.titleTextView.setText(title);
		holder.noteTextView.setText(note);
		holder.infoTextView.setText(info);

		// Set image
		if (infoImage != 0) {
			holder.infoImageView.setImageResource(infoImage);
		}

		// Set visibilities
		holder.titleTextView.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
		holder.noteTextView.setVisibility(TextUtils.isEmpty(note) ? View.GONE : View.VISIBLE);
		holder.infoLayout.setVisibility(TextUtils.isEmpty(info) ? View.GONE : View.VISIBLE);

		// Set padding
		int paddingTop = (holder.titleTextView.getVisibility() != View.VISIBLE) ? 0
				: holder.itemView.getContext().getResources()
						.getDimensionPixelSize(R.dimen.note_content_spacing);
		holder.noteTextView.setPadding(holder.noteTextView.getPaddingLeft(), paddingTop,
				holder.noteTextView.getPaddingRight(), holder.noteTextView.getPaddingBottom());

		// Set background color
		((CardView) holder.itemView).setCardBackgroundColor(color);

		//서지현
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "postition : " + position, Toast.LENGTH_SHORT).show();
				//delete(position);
				AlertDialog.Builder alertDlg = new AlertDialog.Builder(context);

				///4/30 추가: 공지내용보기 back key - 리스트뷰 배경색 변경
				alertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
					public boolean onKey(DialogInterface dialog,
										 int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							dialog.dismiss();
							return true;
						}
						return false;
					}
				});

				////4/30 추가: 공지내용보기 title 배경색 설정 - xml 생성*
				//title 만 설정

				String service = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater inflater = (LayoutInflater)context.getSystemService (service);
				View view1 = inflater.inflate(R.layout.dialog_notice_content, null);
				alertDlg.setCustomTitle(view1);

				//공지내용 가져오기
				NoticeData item = dataManager.notices.get(position);
				String content = item.getNotice_content();
				alertDlg.setMessage(content);

				alertDlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				alertDlg.show();
			}
		});

	}

	@Override
	public int getItemCount() {
		//return notes.size();
		return dataManager.notices.size();
	}

	private ArrayList<Note> generateNotes(Context context) {

		ArrayList<Note> notes = new ArrayList<Note>();
		for (int i = 0; i < dataManager.notices.size(); i++) {
			notes.add(Note.getNoteFromFindapple(context, dataManager.notices.get(i).getNotice_title(), dataManager.notices.get(i).getNotice_content()));
		}
		notifyDataSetChanged();
		return notes;

		/*
		ArrayList<Note> notes = new ArrayList<Note>();
		for (int i = 0; i < numNotes; i++) {
			notes.add(Note.randomNote(context));
		}
		return notes;
*/
	}
	public void updateData(Context context) {
		notes = generateNotes(context);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder{

		public TextView titleTextView;
		public TextView noteTextView;
		public LinearLayout infoLayout;
		public TextView infoTextView;
		public ImageView infoImageView;

		public ViewHolder(View itemView) {
			super(itemView);
			titleTextView = (TextView) itemView.findViewById(R.id.note_title);
			noteTextView = (TextView) itemView.findViewById(R.id.note_text);
			infoLayout = (LinearLayout) itemView.findViewById(R.id.note_info_layout);
			infoTextView = (TextView) itemView.findViewById(R.id.note_info);
			infoImageView = (ImageView) itemView.findViewById(R.id.note_info_image);
		}


	}

}
