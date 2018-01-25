# OpenCV-Tracker
An application-specific Java implementation of OpenCV for tracking objects on screen in real time.

<hr>
This application tracks the position of two characters who are sparring in an online multiplayer game through Google Chrome.
<br>
<br>
The program features:<br>
<ul>
  <li>Frame-by-frame screen capturing for analysis, comparing the screenshot of the current frame with the previous frame in order to find the differences.</li>
  <li>Dilation and Erosion of resulting difference image to minimize noise.</li>
  <li>Contours to outline the bounding box of the sparring players using the minimized noise image.</li>
  <li>Box representing the player drawn accurately at player's position at a certain point in time.</li>
  <li>GUI with options to show/hide boxes, contours, and difference image.</li>
</ul>

# Example GIF
![GIF](https://thumbs.gfycat.com/QuarterlyElasticHuemul-size_restricted.gif)
