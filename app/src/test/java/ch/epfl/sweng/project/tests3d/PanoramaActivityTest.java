package ch.epfl.sweng.project.tests3d;


import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ch.epfl.sweng.project.BuildConfig;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PanoramaActivityTest {
/*
    @Mock
    private PInterface mockedParseManager;
    @Mock
    private HouseManager mockedHouseManager;
    @Mock
    private SurfaceView mockedSurface;
    @InjectMocks
    private PanoramaActivity panoramaActivity;

    private ActivityController<PanoramaActivity> activityController;

    @Before
    public void initMocks(){

        activityController = Robolectric.buildActivity(PanoramaActivity.class);
        // get the activity instance
        panoramaActivity = activityController.get();

        MockitoAnnotations.initMocks(this);
        when(mockedParseManager.getHouseManager((String) isNull(), any(Context.class))).thenReturn
                (mockedHouseManager);

    }

    @Test
    public void serverErrorClosesActivity(){
        when(mockedHouseManager.getStartingId()).thenReturn(0);
        activityController.create();
        verify(mockedParseManager, times(1)).getHouseManager((String) isNull(), any(Context.class));
    }
*/
}
