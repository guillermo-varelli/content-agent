package com.n.in.service;

import com.n.in.model.Step;

public interface IAClientStrategy {
    Object generate(Step step);
}
