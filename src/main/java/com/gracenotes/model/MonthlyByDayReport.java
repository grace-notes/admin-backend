package com.gracenotes.model;

import lombok.Data;

import java.util.List;

/**
 * Created by adam on 6/7/15.
 */
@Data
public class MonthlyByDayReport {
    String subtitle;
    List<String> daysInMonth;
    List<Integer> registrationsByDay;
    List<Integer> courseRequestsByDay;
}
