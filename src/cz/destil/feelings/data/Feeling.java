package cz.destil.feelings.data;

import java.io.Serializable;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Data object for one feeling
 * 
 * @author Destil
 */
public class Feeling implements Serializable {
	private static final long serialVersionUID = 2795826801404948486L;
	public int feeling; // value 0-8
	public long created;
	public String note;

	public Feeling(int feeling, long datetime, String note) {
		this.feeling = feeling;
		this.created = datetime;
		this.note = note;
	}

	public static final class Feelings implements BaseColumns {

		@SuppressWarnings("unused")
		private static final String TAG = "Payments";

		private Feelings() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://" + Database.AUTHORITY + "/feelings");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.destil.feeling";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.destil.feeling";

		public static final String _ID = "_id";

		public static final String FEELING = "feeling";

		public static final String CREATED = "created";

		public static final String NOTE = "note";
	}
}
