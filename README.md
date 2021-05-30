# b-plus-tree-on-disk-java

This project is the implementaion of B+Trees on disk. B+Tree is the most well known index used mainly in databases.
The internal and the leaf nodes are stored in disk and retrieved when it is needed. The main difference from the in-memory
implementation is that the internal nodes point to disk pages instead of node instances. Furthermore, the leaves point to pages of another 
file (data file), where the values of keys are stored. The following image depicts the above scenario. 

![image](https://user-images.githubusercontent.com/25617530/120104754-b43e4b00-c15e-11eb-8820-05adb1a9e645.png)
