package com.example.samsungapp.views;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.samsungapp.Model.QuestionModel;
import com.example.samsungapp.R;
import com.example.samsungapp.viewmodel.QuestionViewModel;

import java.util.HashMap;
import java.util.List;


public class QuizFragment extends Fragment implements View.OnClickListener {

    private QuestionViewModel viewModel;
    private NavController navController;
    private ProgressBar progressBar;
    private Button option1Btn , option2Btn , option3Btn , nextQueBtn;
    private TextView questionTv  , questionNumber , timerCountTv;
    private ImageView closeQuizBtn;
    private String quizId;
    private long totalQuestions;
    private int currentQueNo = 0;
    private boolean canAnswer = false;
    private long timer;
    private CountDownTimer countDownTimer;
    private int notAnswerd = 0;
    private int correctAnswer = 0;
    private int wrongAnswer = 0;
    private String answer = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(QuestionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_quiz,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view); //Контроллер для перехода

        //Идентификация всех элементов на экране
        closeQuizBtn = view.findViewById(R.id.close_quiz_btn);
        option1Btn = view.findViewById(R.id.option1);
        option2Btn = view.findViewById(R.id.option2);
        option3Btn = view.findViewById(R.id.option3);
        nextQueBtn = view.findViewById(R.id.sledQuizBtn);
        questionTv = view.findViewById(R.id.questionQuiz);
        timerCountTv = view.findViewById(R.id.rezult_procent);
        questionNumber = view.findViewById(R.id.number_vopros);
        progressBar = view.findViewById(R.id.quiz_progress_itog);

        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        totalQuestions = QuizFragmentArgs.fromBundle(getArguments()).getTotalQueCount();
        viewModel.setQuizId(quizId);
        viewModel.getQuestion();

        option1Btn.setOnClickListener(this);
        option2Btn.setOnClickListener(this);
        option3Btn.setOnClickListener(this);
        nextQueBtn.setOnClickListener(this);

        //Нажатие на кнопку выхода (крестик)
        closeQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_quizragment_to_listFragment);
            }
        });

        loadData();
    }

    private void loadData(){
        enableOptions();
        loadQuestions(1);
    }

    private void enableOptions(){
        option1Btn.setVisibility(View.VISIBLE);
        option2Btn.setVisibility(View.VISIBLE);
        option3Btn.setVisibility(View.VISIBLE);



        option1Btn.setEnabled(true);
        option2Btn.setEnabled(true);
        option3Btn.setEnabled(true);


        nextQueBtn.setVisibility(View.INVISIBLE);
    }

    private void loadQuestions(int i){

        currentQueNo = i;
        viewModel.getQuestionMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuestionModel>>() {
            @Override
            public void onChanged(List<QuestionModel> questionModels) {
                questionTv.setText(String.valueOf(currentQueNo) + ") " + questionModels.get(i - 1).getQuestion());
                option1Btn.setText(questionModels.get(i - 1).getOption_a());
                option2Btn.setText(questionModels.get(i - 1).getOption_b());
                option3Btn.setText(questionModels.get(i - 1).getOption_c());
                timer = questionModels.get(i-1).getTimer();
                answer = questionModels.get(i-1).getAnswer();


                questionNumber.setText(String.valueOf(currentQueNo));
                startTimer();
            }
        });

        canAnswer = true;
    }

    private void startTimer(){
        timerCountTv.setText(String.valueOf(timer));
        progressBar.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(timer * 1000 , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timerCountTv.setText(millisUntilFinished / 1000 + "");

                Long percent = millisUntilFinished/(timer*10);
                progressBar.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                canAnswer = false;
                notAnswerd ++;
                showNextBtn();
            }
        }.start();
    }

    //Появление кнопки далее
    private void showNextBtn() {
        if (currentQueNo == totalQuestions){
            nextQueBtn.setText("Далее");
            nextQueBtn.setEnabled(true);
            nextQueBtn.setVisibility(View.VISIBLE);
        }else{
            nextQueBtn.setVisibility(View.VISIBLE);
            nextQueBtn.setEnabled(true);
        }
    }

    //Нажатие на варианты ответов
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.option1:
                verifyAnswer(option1Btn);
                break;
            case R.id.option2:
                verifyAnswer(option2Btn);
                break;
            case R.id.option3:
                verifyAnswer(option3Btn);
                break;
            case R.id.sledQuizBtn:
                if (currentQueNo == totalQuestions){
                    submitResults();
                }else{
                    currentQueNo ++;
                    loadQuestions(currentQueNo);
                    resetOptions();
                }
                break;
        }
    }

    //Настройка цвета кнопок
    private void resetOptions(){
        nextQueBtn.setVisibility(View.INVISIBLE);
        nextQueBtn.setEnabled(false);
        option1Btn.setBackground(ContextCompat.getDrawable(getContext() , R.color.blue));
        option2Btn.setBackground(ContextCompat.getDrawable(getContext() , R.color.blue));
        option3Btn.setBackground(ContextCompat.getDrawable(getContext() , R.color.blue));
    }

    private void submitResults() {
        HashMap<String , Object> resultMap = new HashMap<>();
        resultMap.put("Правильно!" , correctAnswer);
        resultMap.put("Неправильно!" , wrongAnswer);
        resultMap.put("Нет ответа" , notAnswerd);

        viewModel.addResults(resultMap);

        QuizFragmentDirections.ActionQuizragmentToResultFragment action =
                QuizFragmentDirections.actionQuizragmentToResultFragment();
        action.setQuizId(quizId);
        navController.navigate(action);

    }

    //Реакция на нажатие кнопки
    private void verifyAnswer(Button button){
        if (canAnswer){
            if (answer.equals(button.getText())){
                button.setBackground(ContextCompat.getDrawable(getContext() , R.color.green));
                correctAnswer++;
            }else{
                button.setBackground(ContextCompat.getDrawable(getContext() , R.color.red));
                wrongAnswer++;
            }
        }
        canAnswer=false;
        countDownTimer.cancel();
        showNextBtn();
    }
}