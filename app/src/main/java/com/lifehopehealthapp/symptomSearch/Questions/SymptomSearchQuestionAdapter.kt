package com.lifehopehealthapp.symptomSearch.Questions

import android.animation.Animator
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.Questionses
import com.lifehopehealthapp.ResponseModel.SelectQuesAnsModel
import org.greenrobot.eventbus.EventBus
import kotlin.collections.ArrayList


class SymptomSearchQuestionAdapter(
    symptomSearchActivity: SymptomSearchActivity,
    data: ArrayList<Questionses>?,
    selectedData: ArrayList<String>?,
    row: Int,
    recyclerView: RecyclerView
) : RecyclerView.Adapter<SymptomSearchQuestionAdapter.ViewHolder>() {

    private var mContext: Context? = null
    private var mSelectedData: ArrayList<String>? = null
    private var mSelectedAnswer: ArrayList<String>? = ArrayList<String>()
    private var getRecyclerView: RecyclerView? = null
    private var mData: ArrayList<Questionses>? = null
    private var row_index: Int = 1
    private var rowIndex: Int =0;
    private var question: String= "";
    private var imageUrl: String= "";


    init {
        getRecyclerView = recyclerView
        mContext = symptomSearchActivity
        mData = data
        mSelectedData = selectedData
        rowIndex = row;
        question = mData!![0].question!!;
        imageUrl = mData!![0].getImagePath()!!;
//        mSelectedAnswer!!.add("replace")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_questions, parent, false)
        return ViewHolder(v)
    }

    fun loadAnswer(answers: String) {
        if (row_index == mData!!.size + 1) {
//            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show()
        } else {
            /*if(mSelectedAnswer!!.equals("replace")){
                mSelectedAnswer!![0] = answers

            }else{
                mSelectedAnswer!!.add(answers)

            }*/
//            mSelectedData!!.add(answers + "replace")
            notifyDataSetChanged()

            if (row_index != mData!!.size)
                loadNextQuestion(row_index++)
        }


    }

    private fun loadNextQuestion(position: Int) {
        Log.e("loadNextQuestion", position.toString())
//        mData!![position].question?.let { mSelectedData!!.add(it) }

        mData!![position].question?.let { question=it }

        mData!![position].getImagePath()?.let { imageUrl=it }


        notifyDataSetChanged()
        getRecyclerView!!.scrollToPosition(position)
        getRecyclerView!!.smoothScrollToPosition(itemCount - 1)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.textViewDay.text = mData!![position].question

        /* YoYo.with(Techniques.FadeIn).duration(1000)
             .withListener(object : Animator.AnimatorListener {
                 override fun onAnimationStart(animation: Animator) {}
                 override fun onAnimationEnd(animation: Animator) {
                     holder.txtQuestion.text = mSelectedData!![position]
                 }

                 override fun onAnimationCancel(animation: Animator) {}
                 override fun onAnimationRepeat(animation: Animator) {}
             }).playOn(holder.txtQuestion)*/

//        holder.imageViewSymptom.visibility=View.GONE

        holder.imageViewSymptom.loadSvg(imageUrl)
        holder.txtQuestion.text = question

        YoYo.with(Techniques.FadeInUp).duration(1000)
            .withListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {

                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).playOn(holder.imageViewSymptom)


        YoYo.with(Techniques.SlideInRight).duration(1000)
            .withListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).playOn(holder.txtQuestion)


        Handler(Looper.getMainLooper()).postDelayed({
            holder.txtQuestion.visibility = View.VISIBLE
            val loadQues = SelectQuesAnsModel()
            loadQues.mSelectData = "QuestionLoader"
            loadQues.mSelectItem = 2
            EventBus.getDefault().post(loadQues)

            getRecyclerView!!.scrollToPosition(position)
            getRecyclerView!!.smoothScrollToPosition(itemCount - 1)
        }, 50)


       /* Log.e("Ques", mSelectedData!![position])
        if (mSelectedData!![position].contains("replace")) {
            holder.txtQuestion.visibility = View.GONE
//            holder.txtAnswer.visibility = View.VISIBLE
//            holder.txtAnswer.text = mSelectedData!![position].replace("replace", "")
        } else {
            holder.txtQuestion.text = mSelectedData!![position]
//            holder.txtAnswer.visibility = View.GONE
            if (position == mSelectedData!!.size - 1) {


                holder.txtQuestion.visibility = View.GONE

                Handler(Looper.getMainLooper()).postDelayed({
                    holder.txtQuestion.visibility = View.VISIBLE
                    val loadQues = SelectQuesAnsModel()
                    loadQues.mSelectData = "QuestionLoader"
                    loadQues.mSelectItem = 2
                    EventBus.getDefault().post(loadQues)

                    getRecyclerView!!.scrollToPosition(position)
                    getRecyclerView!!.smoothScrollToPosition(itemCount - 1)
                }, 300)

            } else {
                holder.txtQuestion.visibility = View.VISIBLE
            }
        }*/

        /*  if(mSelectedAnswer!!.isNotEmpty()){
             /* if(mSelectedAnswer!!.equals("replace")){
                  holder.txtAnswer.visibility = View.GONE
              }else{
                  holder.txtAnswer.visibility = View.VISIBLE
              }*/
              holder.txtAnswer.visibility = View.VISIBLE
              holder.txtAnswer.text = mSelectedData!![position]
          }*/
/*

        mSelectedAnswer!!.isNotEmpty()?.let {
            holder.txtAnswer.visibility = View.VISIBLE
              holder.txtAnswer.text = mSelectedAnswer!![position]
        }
*/


    }
    fun ImageView.loadSvg(url: String?) {
        /*val requestBuilder: RequestBuilder<PictureDrawable> = GlideToVectorYou
            .init()
            .setPlaceHolder(0,R.drawable.ic_no_image)
            .with(this.context)
            .requestBuilder

        requestBuilder
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(50))
            .apply(RequestOptions())
            .diskCacheStrategy()
            .skipMemoryCache(true)
            .into(this)*/
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(0, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtQuestion = itemView.findViewById(R.id.txt_question) as AppCompatTextView
//        val txtAnswer = itemView.findViewById(R.id.txt_answer) as AppCompatTextView
       val imageViewSymptom : AppCompatImageView = itemView.findViewById(R.id.imageViewSymptom)
    }
}