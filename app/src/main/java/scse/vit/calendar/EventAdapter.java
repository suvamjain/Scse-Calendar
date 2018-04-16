package scse.vit.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private JSONObject mDataset;
    private SparseArray<String> mDateSet;
    private Context mContext;
    private Random rand;

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView Date;
        private LinearLayout Details;
        private TextView weekday;
        //private TextView date_suffix;

        private ViewHolder(View v)
        {
            super(v);
            //date_suffix=v.findViewById(R.id.card_datesuffix);
            Date=v.findViewById(R.id.card_date);
            weekday=v.findViewById(R.id.card_day);
            Details=v.findViewById(R.id.card_details);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public EventAdapter(Context myContext,JSONObject myDataset, SparseArray<String> myDateSet) {
        rand = new Random();
        mContext=myContext;
        mDataset = myDataset;
        mDateSet = myDateSet;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);

        return new ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String dt = mDateSet.valueAt(position);
        DateFormat df = new SimpleDateFormat("y-M-d");
        try {
            Date d1=df.parse(dt);
            DateFormat text_date=new SimpleDateFormat("d");
            DateFormat text_weekday=new SimpleDateFormat("E");
            DateFormat text_month= new SimpleDateFormat("MMMM");
            DateFormat text_year= new SimpleDateFormat("yyyy");




            //holder.Date.setText(String.valueOf(text_date.format(d1)));
            holder.weekday.setText(String.valueOf(text_weekday.format(d1)));
            //holder.date_suffix.setText(getDayNumberSuffix(Integer.parseInt(String.valueOf(text_date.format(d1)))));

            SpannableString Dates = new SpannableString((String.valueOf(text_date.format(d1))).concat(
                    getDayNumberSuffix(Integer.parseInt(String.valueOf(text_date.format(d1))))
            ));
            Dates.setSpan(new RelativeSizeSpan(2f),0,(String.valueOf(text_date.format(d1))).length(),0);

            holder.Date.setText(Dates);
            Log.d("===??==date=",Dates.toString());

            String[] allColors = mContext.getResources().getStringArray(R.array.card_color);
            int color_index=rand.nextInt(allColors.length);

            holder.Date.setBackgroundColor(Color.parseColor(allColors[color_index]));
            holder.weekday.setBackgroundColor(Color.parseColor(allColors[color_index]));



            JSONArray tmp=mDataset.getJSONArray(mDateSet.get(position));
            JSONObject temp=tmp.getJSONObject(0);
            holder.Details.removeAllViews();
            for( int i = 0; i < temp.length(); i++ ) {


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(2,0,0,2);

                TextView textView = new TextView(mContext);
                textView.setLayoutParams(layoutParams);
                textView.setText(Html.fromHtml(temp.get(Integer.toString(i)).toString()));
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                textView.setPadding(8, 5, 0, 0);
                //Log.d("--Ad--",temp.get(Integer.toString(i)).toString());
                holder.Details.addView(textView);



            }


        } catch (ParseException e) {
            e.printStackTrace();
        }catch (JSONException e) {
                e.printStackTrace();
         }




    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDateSet.size();
    }

    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}