package edu.wgu.hreid6.wgugo;

import edu.wgu.hreid6.wgugo.data.model.WguEvent;

/**
 * Created by hreid on 2/11/17.
 */

public interface Schedulable {
    WguEvent getWguEvent();
    String getEventKey();
    boolean isScheduleable();
}

interface Sharable extends Schedulable {

}
