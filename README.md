# Fall-Research-2022
 
 Title: Applications of machine learning in logistics systems

Abstract:
Inverse kinematics (IK) is defined as the problem of determining a set of joint positions that result in the end effector of a chain or series of segments being moved to a desired position. The set of movements resulting from the solution of an IK problem should reach the desired position smoothly, rapidly, and as accurately as possible. IK problems are commonly solved by an analytical or numeric approach. In an analytical approach, the solution is achieved by utilizing trigonometric functions. The analytical approach is only capable of solving IK problems with a finite set of solutions, the numeric approach is widely preferred in these problems. In a numeric approach, the solution is approximated iteratively by solving a series of matrices until the approximated error of the result reaches a value near zero. Both methods currently require high computational cost for each solution and can result in inaccurate or abrupt motions. A novel solution to this problem can be found in machine learning. This research seeks to create a model through reinforcement learning that is capable of solving complex IK systems. In the model created, rewards are given for behaviors that result in locally smooth, accurate, and rapid movement to the target position. Angular constraints in this model are enforced through negative rewards. Using machine learning over traditional methods, convergence on IK solutions is achieved faster and more accurately with less computational cost per solution at the expense of a higher computational cost during training. Additionally, this results in a more versatile model, capable of reaching IK solutions with any configuration of chain segments or degrees of freedom.

BOM:

 V1: 
  - Some early work towards refining control for the arm and rendering changes to the arm's position

 V2:
  - Transition to Unity and MQTT, tensorflow bits and pieces that did not make it into the final project

 final:
  - A fully autonomous inverse kinematics solver using a cnn model trained via reinforcement learning in Unity ML Agents
  - A simulation in Unity to render object in relation to the arm and the arms current position
  - A mqtt broker service to facilitate communication between Unity and the ESP 32
  - ESP32 controller bits and pieces:
    - arm controller for the 6DOF arm
    - simple web server to transmit camera data
  - python script to detect objects from the esp32 camera data
