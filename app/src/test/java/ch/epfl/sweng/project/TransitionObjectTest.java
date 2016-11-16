package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.data.TransitionObject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TransitionObjectTest {

    @Test
    public void TransitionTest(){
        TransitionObject angleMapping1 = new TransitionObject(1.0,2.0,1,"hola");
        assertThat(angleMapping1.equals(null), is(false));
        TransitionObject angleMapping2 = new TransitionObject(1.0,2.0,1,"hola");
        assertThat(angleMapping1.equals(angleMapping2), is(true));
        TransitionObject angleMapping3 = new TransitionObject(1.0,2.0,1,"Sanchez");
        assertThat(angleMapping1.equals(angleMapping3), is(false));
    }

}
