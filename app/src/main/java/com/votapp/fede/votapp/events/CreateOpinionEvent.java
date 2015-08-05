package com.votapp.fede.votapp.events;

import com.votapp.fede.votapp.domain.Opinion;

/**
 * Created by fede on 27/7/15.
 */
public class CreateOpinionEvent {

    private Opinion opinion;

    public CreateOpinionEvent(Opinion opinion){
        this.opinion = opinion;
    }

    public Opinion getOpinion() {
        return opinion;
    }

    public void setOpinion(Opinion opinion) {
        this.opinion = opinion;
    }
}
