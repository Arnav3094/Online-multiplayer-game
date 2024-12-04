package androidsamples.java.tictactoe;

import androidx.lifecycle.ViewModel;


public class GameViewModel extends ViewModel {
    public Integer popCnt  = 0;
    public Integer scoreUpdated  = 0;

    public void reset(){
        popCnt = 0;
        scoreUpdated =0;
    }
}
