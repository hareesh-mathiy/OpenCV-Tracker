# OpenCV-Tracker
An application-specific Java implementation of OpenCV for tracking objects on screen in real time.

<hr>
This application tracks the position of two characters who are sparring in an online multiplayer game through Google Chrome. Players come in multiple different looks and outfits due to customization, so tracking a certain section of the player was not possible since it would not apply to a general case. Therefore, I wrote this program to track the frame-by-frame differences on the screen in order to find the player's position relative to the arena. (Note: the tracking area is very specific and is hard-coded to a certain spot in the screen for my set-up. This may be changed in the future to allow compatibility with other set-ups).
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
<br>
Now that a relatively accurate way to capture the player's position is possible, the next steps for this project include collecting and analyzing the data of multiple players sparring each other in order to create an AI which can spar in a similar style using learning algorithms.
<hr>


# Example GIF
![GIF](https://thumbs.gfycat.com/QuarterlyElasticHuemul-size_restricted.gif)
