package com.jmonte.seriesquotes.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DBAdapter {

	public static final String KEY_ROWID = "id";
	public static final String KEY_QUOTE = "quote";
	public static final String KEY_AUTHOR_ID = "author_id";
	public static final String KEY_CATEGORY_ID = "category_id";
	public static final String KEY_MY_CATEGORY = "mycategory";
	public static final String KEY_NAME = "name";
	public static final String KEY_FAVORITE = "favorite";
	
	private static final String TAG = "DBAdapter";
	
	private static final String DATABASE_NAME = "quotes";
	private static final String DATABASE_TABLE_QUOTE = "quotes";
	private static final String DATABASE_TABLE_CATEGORY = "categories";
	private static final String DATABASE_TABLE_AUTHOR = "authors";
	private static final String DATABASE_TABLE_CONFIG = "config";
	
	private static final int DATABASE_VERSION = 2;
	
	private static final String DATABASE_CREATE_QUOTE = 
		"CREATE TABLE "+DATABASE_TABLE_QUOTE +
			"( id integer primary key," +
			"quote text not null, author_id integer not null," +
			"category_id text not null," +
			"favorite integer );";
	private static final String DATABASE_CREATE_CATEGORY = 
		"CREATE TABLE "+DATABASE_TABLE_CATEGORY +
			"(id integer primary key," +
			"name text not null," +
			"mycategory integer);";
	private static final String DATABASE_CREATE_AUTHOR = 
		"CREATE TABLE "+DATABASE_TABLE_AUTHOR +
			"(id integer primary key," +
			"name text not null);";
	private static final String DATABASE_CREATE_CONFIG = 
		"CREATE TABLE " +DATABASE_TABLE_CONFIG +
		" (quote_id integer," +
		"  category_id integer," +
		"  author_id integer," +
		"  frequency integer );";
	
	Context context = null;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter() {
		
		DBHelper = new DatabaseHelper(context);
	}
	
	public DBAdapter (Context context) {
		this.context = context;		
		DBHelper = new DatabaseHelper(context);
	}
	
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();	
		return this;
		
	}
	
	public void close() {
		DBHelper.close();
	}
	
	public long insertQuote(int id,String quote, int author_id , int category_id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID,id);
		initialValues.put(KEY_QUOTE,quote);
		initialValues.put(KEY_AUTHOR_ID,author_id);
		initialValues.put(KEY_CATEGORY_ID,category_id);
		return db.insert(DATABASE_TABLE_QUOTE, null, initialValues);
	}
	
	public long insertCategory(int id, String name ) { 
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID,id);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_MY_CATEGORY, 1);
		return db.insert(DATABASE_TABLE_CATEGORY, null, initialValues);
	}
	
	public long insertAuthor(int id, String name) { 
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID,id);
		initialValues.put(KEY_NAME, name);
		return db.insert(DATABASE_TABLE_AUTHOR, null, initialValues);
	}
	
	public boolean isInMyCategory(int id) {
		return db.query(DATABASE_TABLE_CATEGORY, 
				new String[] {
				KEY_ROWID,
			}, 
			"id="+id+ " AND mycategory=1",
			null,
			null, 
			null, 
			null).getCount() > 0;
	}
	
	public void insertMyCategory(int id) {
		System.out.println("Insert: "+id);
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MY_CATEGORY,1);
		db.update(DATABASE_TABLE_CATEGORY, initialValues, "id="+id,null);
	}

	public void insertMyCategory(String name) {
		System.out.println("Insert: "+name);
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MY_CATEGORY,1);
		db.update(DATABASE_TABLE_CATEGORY, initialValues, "name='"+name+"'",null);
	}
	
	public void insertMyCategories(String[] names) {
		String condition = "";
		for(int i = 0;i < names.length;i++) {
			if(condition.length() > 0) {
				condition += " OR ";
			}
			condition += "categories.name='"+names[i]+"'";
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MY_CATEGORY,0);
		db.update(DATABASE_TABLE_CATEGORY, initialValues, null,null);
		initialValues.put(KEY_MY_CATEGORY,1);
		db.update(DATABASE_TABLE_CATEGORY, initialValues, condition,null);
	}
	
	public void deleteMyCategory(int id) {
		System.out.println("Delete: "+id);
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MY_CATEGORY,0);
		db.update(DATABASE_TABLE_CATEGORY, initialValues, "id="+id,null);
	}

	public void deleteMyCategory(String name) {
		System.out.println("Delete: "+name);
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MY_CATEGORY,0);
		db.update(DATABASE_TABLE_CATEGORY, initialValues, "name='"+name+"'",null);
	}
	
	
	public boolean deleteTitle(long rowId) {
		return db.delete(DATABASE_TABLE_QUOTE, KEY_ROWID +"="+ rowId, null) > 0;
	}
	
	public ArrayList<Integer> getMyCategories() {
		ArrayList<Integer> myCategories = new ArrayList<Integer>();
		Cursor c =db.query(DATABASE_TABLE_CATEGORY, 
				new String[] {
					KEY_ROWID,
				}, 
			"mycategory=1",
			null,
			null, 
			null, 
			null);
		System.out.println(c.getCount());
		if (c.moveToFirst()) { 
	         do {          
	        	 myCategories.add(c.getInt(0));
	         } while (c.moveToNext());
	        }
		return myCategories;
	}
	
	public Cursor getAllCategories(String OrderBy) {
		return db.query(DATABASE_TABLE_CATEGORY, 
				new String[] {
					KEY_ROWID,
					KEY_NAME,
				}, 
			null,
			null,
			null, 
			null, 
			OrderBy);
	}

	public Cursor getAllAuthors(String OrderBy) {
		return db.query(DATABASE_TABLE_AUTHOR, 
				new String[] {
					KEY_ROWID,
					KEY_NAME,
				}, 
			null,
			null,
			null, 
			null, 
			OrderBy);
	}	
	public Cursor getAllQuotes() {
		Cursor c= db.query(DATABASE_TABLE_QUOTE +","+DATABASE_TABLE_CATEGORY+","+DATABASE_TABLE_AUTHOR, 
				new String[] {
				DATABASE_TABLE_QUOTE+"."+KEY_ROWID,
				KEY_QUOTE,
				DATABASE_TABLE_CATEGORY+"."+KEY_NAME,
				DATABASE_TABLE_AUTHOR+"."+KEY_NAME,
			}, 
		"quotes.category_id=categories.id AND quotes.author_id=authors.id",
		null,
		null, 
		null, 
		null);
		return c;
	}
	
	
	public boolean isInFavorites(int id) {
		return db.query(DATABASE_TABLE_QUOTE, 
				new String[] {
				KEY_ROWID,
			}, 
			"id="+id+ " AND favorite=1",
			null,
			null, 
			null, 
			null).getCount() > 0;
	}
	
	
	public void insertFavorite(int id) {
		System.out.println("INSERT FAVORITE:"+id);
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_FAVORITE,1);
		db.update(DATABASE_TABLE_QUOTE, initialValues, "id="+id,null);
	}
	
	public void deleteFavorite(int id) {
		System.out.println("DELETE FAVORITE:"+id);
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_FAVORITE,0);
		db.update(DATABASE_TABLE_QUOTE, initialValues, "id="+id,null);
	}
	
	public Cursor getAllFavorites(String sort) {
		Cursor c= db.query(DATABASE_TABLE_QUOTE +","+DATABASE_TABLE_CATEGORY+","+DATABASE_TABLE_AUTHOR, 
				new String[] {
				DATABASE_TABLE_QUOTE+"."+KEY_ROWID,
				KEY_QUOTE,
				DATABASE_TABLE_CATEGORY+"."+KEY_NAME,
				DATABASE_TABLE_AUTHOR+"."+KEY_NAME,
			}, 
		"quotes.favorite=1 AND quotes.category_id=categories.id AND quotes.author_id=authors.id",
		null,
		null, 
		null, 
		sort);
		return c;
	}

	public ArrayList<Integer> getRandomQuotesId() {
		Cursor mCursor =  db.query(DATABASE_TABLE_QUOTE +","+DATABASE_TABLE_CATEGORY+","+DATABASE_TABLE_AUTHOR, 
					new String[] {
						DATABASE_TABLE_QUOTE+"."+KEY_ROWID,
					}, 
				"categories.mycategory=1 AND quotes.category_id=categories.id AND quotes.author_id=authors.id",
				null,
				null, 
				null, 
				"RANDOM(),quotes.id LIMIT 100");
		ArrayList<Integer> quoteList = new ArrayList<Integer>();
		if(mCursor.moveToFirst()) {
			for(int i = 0 ; mCursor.moveToNext();i++) {
				quoteList.add(mCursor.getInt(0));
			}
		}
		return quoteList;
	}

	public Cursor getRandomQuotes() {
		return db.query(DATABASE_TABLE_QUOTE +","+DATABASE_TABLE_CATEGORY+","+DATABASE_TABLE_AUTHOR, 
					new String[] {
						DATABASE_TABLE_QUOTE+"."+KEY_ROWID,
						KEY_QUOTE,
						DATABASE_TABLE_CATEGORY+"."+KEY_NAME,
						DATABASE_TABLE_AUTHOR+"."+KEY_NAME,
					}, 
				"quotes.category_id=categories.id AND quotes.author_id=authors.id",
				null,
				null, 
				null, 
				"RANDOM() LIMIT 20");
	}
	
	public Cursor getQuote(long rowId) throws SQLException {
		return
            db.query(DATABASE_TABLE_QUOTE + ","+DATABASE_TABLE_CATEGORY+ ","+DATABASE_TABLE_AUTHOR , new String[] {
					DATABASE_TABLE_QUOTE+"."+KEY_ROWID,
					KEY_QUOTE,
					DATABASE_TABLE_CATEGORY+"."+KEY_NAME,
					DATABASE_TABLE_AUTHOR+"."+KEY_NAME,
					DATABASE_TABLE_QUOTE+"."+KEY_FAVORITE,
            		}, 
            		DATABASE_TABLE_QUOTE+"."+KEY_ROWID + "=" + rowId + " AND quotes.category_id=categories.id AND quotes.author_id=authors.id", 
            		null,
            		null, 
            		null, 
            		null, 
            		null);
	}
	
	public Cursor getRandomQuote() throws SQLException {
		return
        db.query(DATABASE_TABLE_QUOTE + ","+DATABASE_TABLE_CATEGORY+ ","+DATABASE_TABLE_AUTHOR , new String[] {
				DATABASE_TABLE_QUOTE+"."+KEY_ROWID,
				KEY_QUOTE,
				DATABASE_TABLE_CATEGORY+"."+KEY_NAME,
				DATABASE_TABLE_AUTHOR+"."+KEY_NAME,
        		}, 
        		"quotes.category_id=categories.id AND quotes.author_id=authors.id", 
        		null,
        		null, 
        		null, 
        		"RANDOM() LIMIT 1");
	}
	
	public void setConfig(int quote_id, int category_id, int author_id) {	
		ContentValues initialValues = new ContentValues();
		initialValues.put("quote_id",quote_id);
		initialValues.put("category_id",category_id);
		initialValues.put("author_id",author_id);
		System.out.println(db.update(DATABASE_TABLE_CONFIG,initialValues,"",null));
	}
	
	public void setFrequency(int frequency) {	
		ContentValues initialValues = new ContentValues();
		initialValues.put("frequency",frequency);
		System.out.println(db.update(DATABASE_TABLE_CONFIG,initialValues,"",null));
	}
	
	public int getFrequency() {
		int id = 0;
		Cursor mCursor =  db.query(DATABASE_TABLE_CONFIG, 
					new String[] {
						"frequency",
					}, 
				"1=1",
				null,
				null, 
				null, 
				null);
		if(mCursor.moveToFirst()) {
			id = mCursor.getInt(0);
		}
		return id;
	}
	
	public int getConfigId(String type) {
		int id = 0;
		Cursor mCursor =  db.query(DATABASE_TABLE_CONFIG, 
					new String[] {
						type+"_id",
					}, 
				"1=1",
				null,
				null, 
				null, 
				null);
		if(mCursor.moveToFirst()) {
			id = mCursor.getInt(0);
		}
		return id;
	}

    public boolean updateTitle(long rowId, String quote) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_QUOTE, quote);
        return db.update(DATABASE_TABLE_QUOTE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
    }

	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
			super(context,DATABASE_NAME, null , DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_QUOTE);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_AUTHOR);
            
			db.execSQL(DATABASE_CREATE_QUOTE);
			db.execSQL(DATABASE_CREATE_CATEGORY);
			db.execSQL(DATABASE_CREATE_AUTHOR);
			db.execSQL(DATABASE_CREATE_CONFIG);
			
			db.execSQL("INSERT INTO config VALUES(0,0,0,5);");
			
			//categories sample data
			db.execSQL("INSERT INTO categories VALUES('3','Age',1);");
			db.execSQL("INSERT INTO categories VALUES('4','Anger',1);");
			db.execSQL("INSERT INTO categories VALUES('5','Art',1);");
			db.execSQL("INSERT INTO categories VALUES('6','Business',1);");
			db.execSQL("INSERT INTO categories VALUES('41','Pick Up Lines',1);");
			db.execSQL("INSERT INTO categories VALUES('62','Points to Ponder',1);");
			db.execSQL("INSERT INTO categories VALUES('63','Strange Facts',1);");
			
			//authors sample data
			db.execSQL("INSERT INTO authors VALUES('43','Anonymous');");
			db.execSQL("INSERT INTO authors VALUES('45','Maurice Chevalier');");
			db.execSQL("INSERT INTO authors VALUES('46','Marcus Tullius Cicero');");
			db.execSQL("INSERT INTO authors VALUES('47','Mark Twain');");
			db.execSQL("INSERT INTO authors VALUES('48','Henry Ford');");
			db.execSQL("INSERT INTO authors VALUES('49','Francis Bacon');");
			db.execSQL("INSERT INTO authors VALUES('50','Ralph Waldo Emerson');");
			db.execSQL("INSERT INTO authors VALUES('51','Buddha');");
			db.execSQL("INSERT INTO authors VALUES('52','Sydney J. Harris');");
			db.execSQL("INSERT INTO authors VALUES('53','George Jean Nathan');");
			db.execSQL("INSERT INTO authors VALUES('54','Phyllis Diller');");
			db.execSQL("INSERT INTO authors VALUES('55','George Santayana');");
			db.execSQL("INSERT INTO authors VALUES('56','Agatha Christie');");
			db.execSQL("INSERT INTO authors VALUES('57','Marshall McLuhan');");
			db.execSQL("INSERT INTO authors VALUES('59','William Feather');");
			db.execSQL("INSERT INTO authors VALUES('60','Alfred A. Montapert');");
			db.execSQL("INSERT INTO authors VALUES('61','Laurence J. Peter');");
			//quotes sample data
			
			insertQuote(db,4,"Kumain ka ba ng asukal? Ang tamis kasi ng ngiti mo.",43,41);
			insertQuote(db,6,"I'm a bee, can you be my honey?",43,41);
			insertQuote(db,8,"May lahi ka bang keyboard? Type kasi kita.",43,41);
			insertQuote(db,10,"Papupulis kita! Ninakaw mo kasi puso ko.",43,41);
			insertQuote(db,11,"Are you a dictionary? Cause you add meaning to my life.",43,41);
			insertQuote(db,13,"I lost my number. Can I have yours?",43,41);
			insertQuote(db,15,"I forgot your name. Can I call you mine?",43,41);
			insertQuote(db,17,"Ice ka ba? Crush kita, okay lang?",43,41);
			insertQuote(db,19,"Meralco ka ba? Pag ngumiti ka kasi may spark.",43,41);
			insertQuote(db,23,"Bangin ka ba? Nahulog kasi ako sa'yo.",43,41);
			insertQuote(db,25,"May butas ba yang puso mo? Natrap kasi ako, can't find my way out.",43,41);
			insertQuote(db,27,"Pustiso ka ba? Kasi I can't smile without you.",43,41);
			insertQuote(db,29,"Nabibingi ka na ba? Coz my heart has been screaming out your name for quite some time now.",43,41);
			insertQuote(db,31,"Tapos na ba ung exam mo? Para ako naman sagutin mo.",43,41);
			insertQuote(db,32,"Ok lang na ako ang magbayad ng tuition fee mo? Basta pag-aralan mo lang akong mahalin.",43,41);
			insertQuote(db,34,"Alam mo ba na scientist ako? At ikaw yung LAB ko.",43,41);
			insertQuote(db,36,"May lisensya ka ba? Coz you're driving me crazy.",43,41);
			insertQuote(db,38,"May kilala ka bang gumagawa ng relo? May sira ata relo ko. Pag ikaw kasi kasama ko, humihinto ang oras ko.",43,41);
			insertQuote(db,40,"Aanhin pa ang gravity, kung lagi lang akong mahuhulog sa iyo?",43,41);
			insertQuote(db,42,"A comfortable old age is the reward of a well-spent youth. Instead of its bringing sad and melancholy prospects of decay, it would give us hopes of eternal youth in a better world. ",45,3);
			insertQuote(db,44,"Advice in old age is foolish; for what can be more absurd than to increase our provisions for the road the nearer we approach to our journey's end. ",46,3);
			insertQuote(db,46,"Age is an issue of mind over matter. If you don't mind, it doesn't matter. ",47,3);
			insertQuote(db,47,"An archaeologist is the best husband a woman can have. The older she gets the more interested he is in her. ",56,3);
			insertQuote(db,49,"Anyone who stops learning is old, whether at twenty or eighty. Anyone who keeps learning stays young. The greatest thing in life is to keep your mind young. ",48,3);
			insertQuote(db,51,"I will never be an old man. To me, old age is always 15 years older than I am. ",49,3);
			insertQuote(db,53," For every minute you remain angry, you give up sixty seconds of peace of mind. ",50,4);
			insertQuote(db,55,"Holding on to anger is like grasping a hot coal with the intent of throwing it at someone else; you are the one who gets burned. ",51,4);
			insertQuote(db,57,"If a small thing has the power to make you angry, does that not indicate something about your size? ",52,4);
			insertQuote(db,59,"Never go to bed mad. Stay up and fight. ",54,4);
			insertQuote(db,62,"No man can think clearly when his fists are clenched. ",53,4);
			insertQuote(db,65,"An artist is a dreamer consenting to dream of the actual world. ",55,5);
			insertQuote(db,77,"Am i a bad shooter? Coz i keep on missing you.",43,41);
			insertQuote(db,78,"Favorite subject mo ba geometry? Kasi kahit anong angle, ang cute mo.",43,41);
			insertQuote(db,80,"Kapag ako may tindahan, lahat ng tao bebentahan ko ng mura, sayo lang hindi, dahil sayo lang ako magmamahal.",43,41);
			insertQuote(db,82,"Alarm clock ka ba? Ginising mo kasi ang natutulog kong puso eh.",43,41);
			insertQuote(db,84,"Pwede ka bang makatabi pag may exam? Cause i feel perfect beside you.",43,41);
			insertQuote(db,86,"May mapa ka ba diyan? Para alam ko ang daan papunta sa puso mo.",43,41);
			insertQuote(db,88,"Minamalat na nanaman puso ko. Paano kasi, laging sinisigaw ang pangalan mo!",43,41);
			insertQuote(db,90,"Pwede ba kitang maging sidecar? Kasi single kasi ako!",43,41);
			insertQuote(db,92,"Umutot ka ba? Kasi you blew me away!",43,41);
			insertQuote(db,94,"Sana T na lang ako, para I'm always next to U.",43,41);
			insertQuote(db,96,"Aanhin pa ang alak kung sa akin pa lang, tinatamaan na sila.",43,41);
			insertQuote(db,98,"Siguro magaling kang mag-CPR kasi napatibok mo ulit ang puso ko!",43,41);
			insertQuote(db,99,"Noong minahal kita, talo mo pa ang traffic sa EDSA. I CANT MOVE ON!",43,41);
			insertQuote(db,100,"Eraser ka ba? Kasi binura mo ang masasamang ala-ala ko.",43,41);
			insertQuote(db,101,"Pwede ba kitang maging driver? Para ikaw na magpatakbo ng buhay ko.",43,41);
			insertQuote(db,103,"Hindi ka ba napapagod, kasi kanina ka pa takbo ng takbo sa utak ko.",43,41);
			insertQuote(db,105,"May free time ka ba? Samahan mo naman ako sa psychiatrist. Magdala daw kasi ako ng kinababaliwan ko.",43,41);
			insertQuote(db,107,"Excuse me. Kung dederetchohin ko ba ang daan na ito, dederetcho ba ito sa puso mo?",43,41);
			insertQuote(db,109,"Mabilis ka siguro sa mga puzzles noh? Kasi kakasimula pa lang ng araw ko binubuo mo na.",43,41);
			insertQuote(db,111,"Uy sabi ng doctor malala na daw ang sakit ko sa puso. Dalawa na lang daw ang option: either ICU or you see me.",43,41);
			insertQuote(db,113,"Ibibili kita ng salbabida, kasi malulunod ka sa pagmamahal ko.",43,41);
			insertQuote(db,115,"Naniniwala ka ba sa love at first sight, o gusto mong dumaan ulit ako?",43,41);
			insertQuote(db,118,"Ads are the cave art of the twentieth century. ",57,5);
			insertQuote(db,120,"An artist is never ahead of his time but most people are far behind theirs. ",43,5);
			insertQuote(db,124,"A budget tells us what we can't afford, but it doesn't keep us from buying it. ",59,6);
			insertQuote(db,126,"All lasting business is built on friendship.",60,6);
			insertQuote(db,128,"An economist is an expert who will know tomorrow why the things he predicted yesterday didn't happen today. ",61,6);
			insertQuote(db,130,"If man evolved from monkeys and apes, why do we still have monkeys and apes?",43,62);
			insertQuote(db,132,"If someone with multiple personalities threatens to kill himself, is it considered a hostage situation?",43,62);
			insertQuote(db,134,"Is there another word for synonym?",43,62);
			insertQuote(db,137,"What do you do when you see an endangered animal eating an endangered plant?",43,62);
			insertQuote(db,139,"Would a fly without wings be called a walk?",43,62);
			insertQuote(db,141,"If a turtle doesn't have a shell, is he homeless or naked?",43,62);
			insertQuote(db,143,"If the police arrest a mime, do they tell him he has the right to remain silent?",43,62);
			insertQuote(db,145,"Before they invented drawing boards, what did they go back to?",43,62);
			insertQuote(db,147,"If all the world is a stage, where is the audience sitting?",43,62);
			insertQuote(db,149,"If the #2 pencil is the most popular, why is it still #2?",43,62);
			insertQuote(db,151,"If you try to fail and you succeed, which have you done? ",43,62);
			insertQuote(db,153,"Why the sun lightens our hair, but darkens our skin? ",43,62);
			insertQuote(db,155,"Why women can't put on mascara with their mouth closed?",43,62);
			insertQuote(db,157,"Why there a light in the fridge and not in the freezer?",43,62);
			insertQuote(db,159,"Why you don't ever see the headline: \"Psychic Wins Lottery\"?",43,62);
			insertQuote(db,161,"Why there isn't mouse flavoured cat food?",43,62);
			insertQuote(db,163,"It's impossible to sneeze with your eyes open. ",43,63);
			insertQuote(db,165,"Leonardo Da Vinci invented the scissors.",43,63);
			insertQuote(db,167,"Maine is the only state whose name is just one syllable. ",43,63);
			insertQuote(db,170,"No word in the English language rhymes with month, orange, silver, or purple. ",43,63);
			insertQuote(db,172,"Our eyes are always the same size from birth, but our nose and ears never stop growing.",43,63);
			insertQuote(db,174,"Rubber bands last longer when refrigerated.",43,63);
			insertQuote(db,176,"'Stewardesses' is the longest word typed with only the left hand and 'lollipop' with your right. ",43,63);
			insertQuote(db,178,"The average person's left hand does 56% of the typing.",43,63);
			insertQuote(db,180,"The microwave was invented after a researcher walked by a radar tube and a chocolate bar melted in his pocket.",43,63);
			insertQuote(db,183,"The sentence: 'The quick brown fox jumps over the lazy dog', uses every letter of the alphabet.",43,63);
			insertQuote(db,185,"The winter of 1932 was so cold that Niagara Falls froze completely solid. ",43,63);
			insertQuote(db,187,"The words 'racecar', 'kayak' and 'level' are palindromes. They read the same whether you read them left to right or right to left. ",43,63);
			insertQuote(db,189,"There are 293 ways to make change for a dollar.",43,63);
			insertQuote(db,191,"There are only four words in the English language which end in 'dous' : tremendous, horrendous, stupendous, and hazardous.",43,63);
			insertQuote(db,193,"There are two words in the English language that have all five vowels in order: 'abstemious' and ' facetious.'",43,63);
			insertQuote(db,195,"Tigers have striped skin, not just striped fur.",43,63);
			insertQuote(db,197,"TYPEWRITER is the longest word that can be made using the letters only on one row of the keyboard. ",43,63);
			insertQuote(db,199,"Women blink nearly twice as much as men. ",43,63);
			insertQuote(db,201,"Your stomach has to produce a new layer of mucus every two weeks; otherwise it will digest itself. ",43,63);
			insertQuote(db,204,"Your body is creating and killing 15 million red blood cells per second!",43,63);
			insertQuote(db,205,"The king of hearts is the only king without a moustache on a standard playing card!",43,63);
			insertQuote(db,207,"There are no clocks in Las Vegas gambling casinos! ",43,63);
			insertQuote(db,209,"The Mona Lisa has no eyebrows. It was the fashion in Renaissance Florence to shave them off",43,63);
			insertQuote(db,211,"The most popular first name in the world is Muhammad!",43,63);
			insertQuote(db,213,"Tablecloths were originally meant to be served as towels with which dinner guests could wipe their hands and faces after eating!",43,63);
			insertQuote(db,215,"When glass breaks, the cracks move faster than 3,000 miles per hour. To photograph the event, a camera must shoot at a millionth of a second!",43,63);
			insertQuote(db,217,"A lightning bolt generates temperatures five times hotter than those found at the sun's surface!",43,63);
			insertQuote(db,219,"A violin contains about 70 separate pieces of wood!",43,63);
			insertQuote(db,221,"It takes glass one million years to decompose, which means it never wears out and can be recycled an infinite amount of times!",43,63);
			insertQuote(db,223,"Forest fires move faster uphill than downhill!",43,63);
			insertQuote(db,225,"Most lipstick contains fish scales!",43,63);
			insertQuote(db,203,"Your body is creating and killing 15 million red blood cells per second!",43,63);
		}

		public long insertQuote(SQLiteDatabase db,int id,String quote, int author_id , int category_id) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_ROWID,id);
			initialValues.put(KEY_QUOTE,quote);
			initialValues.put(KEY_AUTHOR_ID,author_id);
			initialValues.put(KEY_CATEGORY_ID,category_id);
			return db.insert(DATABASE_TABLE_QUOTE, null, initialValues);
		}
		
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                              int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                  + " to "
                  + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_QUOTE);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_AUTHOR);
            onCreate(db);
        }

	}
	
}
