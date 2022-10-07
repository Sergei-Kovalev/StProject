package ru.ngs.summerjob.stproject.validator;

import ru.ngs.summerjob.stproject.domain.children.AnswerChildren;
import ru.ngs.summerjob.stproject.domain.StudentOrder;

public class ChildrenValidator {

    public AnswerChildren checkChildren(StudentOrder so) {
        System.out.println("Children check is running: ");
        AnswerChildren ans = new AnswerChildren();
        return ans;
    }
}
