## What is the project about?
Passive liveness detection with mlkit

## Getting Started
* Clone the repository

## Important Libraries and Frameworks
* Hilt
* MLKIT
* Camera

## Explanation
* This detects slight natural movements on landmarks of faces that are presented to it and uses it to detect liveness,
* It assumes that It is almost impossible for a human that is alive to be completely still, hence the landmarks on their faces will move slightly.
* This is however a very limited assumption, and can still be spoofed by simply simulating slight movements using video or some other means.
* see [FaceAnalyzer](https://github.com/Khalidtoak/passive-liveness-detetction/blob/main/app/src/main/java/com/example/passivelivenessdetetction/FaceAnalyzer.kt) class for more details

https://gist.github.com/Khalidtoak/a5492c07c6e010933a1670971418e5ad
