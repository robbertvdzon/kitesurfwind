Global working:
- A local SQL database will be populated with the wind statistics
- The AppWidget or the Application use the SQL database to show the statistics

How does the local SQL database keeps itself up to date?
- The local SQL database is populated with values from the backend by the SynchronizeWithBackendAsyc Async task
- SynchronizeWithBackendAsyc is started from the SynchronizeWithBackendService service
- The SynchronizeWithBackendService is started automatically every 10 minutes when there are AppWidgets on the screen
- The SynchronizeWithBackendService is started once when the application starts
- When there is a new wind statistics available on the backend, the backends sends a trigger using Google Cloud Messaging (GCM)
- The GcmBroadcastReceiver responds to the GCM messages and starts triggers the SynchronizeWithBackendService

How does the GUI gets updated when there is new data available?
- When the SynchronizeWithBackendAsyc detects that there is new data, it sends a broadcast to the application and to the widgets

Swiping to another spot (fragment) is disabled when touching the wind graph, how does that work?
- The WindViewPager extends the ViewPager and is used in the MainActivity
- The WindViewPager has a property swipe which enables or disabled swiping (by overriding the onInterceptTouchEvent)
- The MainActivity implements a SwipeListener interface with an enableSwipe and disableSwipe function
- The MainActivity enableSwipe and disableSwipe function call the corresponding function on the WindViewPager
- The windGraphView has an onTouch listener. When touched in this view, the MainActivity disableSwipe is called
- The Fragment (WindFragment) also has an onTouch listener. When the MotionEvent.ACTION_UP is detected from any view, the swipe is enabled again

Dependencies:
Make sure the following two dependencies can be found (set in properties-Android under the library tab)
..\..\android-sdks\extras\google\google_play_services\libproject\google-play-services_lib
..\JakeWharton-Android-ViewPagerIndicator-88cd549f\library
