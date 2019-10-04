package fr.cristal.smac.atom;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestMTL.class, WithoutSimulation.class, WithSimpleAgent.class, WithSimulation.class })
public class AllTests {

}
