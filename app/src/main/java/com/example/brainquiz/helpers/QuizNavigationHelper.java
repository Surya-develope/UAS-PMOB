package com.example.brainquiz.helpers;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.brainquiz.R;
import com.example.brainquiz.filter.Soal;
import java.util.List;

/**
 * Helper class untuk menangani navigasi dan tampilan soal dalam quiz
 */
public class QuizNavigationHelper {
    
    private TextView tvSoalNumber, tvQuestion, tvProgress;
    private RadioGroup rgOptions;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnPrevious, btnNext, btnSubmit;
    
    private List<Soal> soalList;
    private List<String> jawabanUser;
    private int currentSoalIndex = 0;
    
    public QuizNavigationHelper(TextView tvSoalNumber, TextView tvQuestion, TextView tvProgress,
                               RadioGroup rgOptions, RadioButton rbA, RadioButton rbB, 
                               RadioButton rbC, RadioButton rbD, Button btnPrevious, 
                               Button btnNext, Button btnSubmit) {
        this.tvSoalNumber = tvSoalNumber;
        this.tvQuestion = tvQuestion;
        this.tvProgress = tvProgress;
        this.rgOptions = rgOptions;
        this.rbA = rbA;
        this.rbB = rbB;
        this.rbC = rbC;
        this.rbD = rbD;
        this.btnPrevious = btnPrevious;
        this.btnNext = btnNext;
        this.btnSubmit = btnSubmit;
    }
    
    public void setSoalData(List<Soal> soalList, List<String> jawabanUser) {
        this.soalList = soalList;
        this.jawabanUser = jawabanUser;
    }
    
    public void displayCurrentSoal() {
        if (soalList == null || soalList.isEmpty() || currentSoalIndex >= soalList.size()) return;

        Soal currentSoal = soalList.get(currentSoalIndex);

        // Update UI
        tvSoalNumber.setText("Soal " + (currentSoalIndex + 1));
        tvQuestion.setText(currentSoal.getQuestion());
        tvProgress.setText((currentSoalIndex + 1) + " dari " + soalList.size());

        // Set options
        rbA.setText("A. " + currentSoal.getOptionA());
        rbB.setText("B. " + currentSoal.getOptionB());
        rbC.setText("C. " + currentSoal.getOptionC());
        rbD.setText("D. " + currentSoal.getOptionD());

        // Clear selection first
        rgOptions.clearCheck();

        // Set previous answer if exists
        String previousAnswer = jawabanUser.get(currentSoalIndex);
        if (!previousAnswer.isEmpty()) {
            switch (previousAnswer) {
                case "A":
                    rbA.setChecked(true);
                    break;
                case "B":
                    rbB.setChecked(true);
                    break;
                case "C":
                    rbC.setChecked(true);
                    break;
                case "D":
                    rbD.setChecked(true);
                    break;
            }
        }
    }
    
    public String saveCurrentAnswer() {
        if (currentSoalIndex >= jawabanUser.size()) return "";

        int selectedId = rgOptions.getCheckedRadioButtonId();
        String answer = "";

        if (selectedId == R.id.rbA) answer = "A";
        else if (selectedId == R.id.rbB) answer = "B";
        else if (selectedId == R.id.rbC) answer = "C";
        else if (selectedId == R.id.rbD) answer = "D";

        jawabanUser.set(currentSoalIndex, answer);
        return answer;
    }
    
    public boolean previousSoal() {
        if (currentSoalIndex > 0) {
            saveCurrentAnswer();
            currentSoalIndex--;
            displayCurrentSoal();
            updateNavigationButtons();
            return true;
        }
        return false;
    }
    
    public boolean nextSoal() {
        if (currentSoalIndex < soalList.size() - 1) {
            saveCurrentAnswer();
            currentSoalIndex++;
            displayCurrentSoal();
            updateNavigationButtons();
            return true;
        }
        return false;
    }
    
    public void updateNavigationButtons() {
        btnPrevious.setEnabled(currentSoalIndex > 0);
        btnNext.setEnabled(currentSoalIndex < soalList.size() - 1);

        // Show submit button on last question
        if (currentSoalIndex == soalList.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }
    }
    
    public int getCurrentSoalIndex() {
        return currentSoalIndex;
    }
    
    public void setCurrentSoalIndex(int index) {
        this.currentSoalIndex = index;
    }
    
    public int getUnansweredCount() {
        int count = 0;
        for (String answer : jawabanUser) {
            if (answer.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
