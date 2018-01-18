# loa - Learning Objects Assessment

Customization for assess learning objects stored in DSpace.

This  development was based on a model for the quality assessment of learning objects which gatered together several approaches and considered the viewpoint of three actors, namely, repository administrators, experts and users. To complete the assessment, the model uses questionaries and checks several metrics (as coherence, consistency, etc) on the metadata that describes the learning objects. The model can be accesed in https://core.ac.uk/download/pdf/19485280.pdf (Spanish only).

The development was integreted to DSpace's JPSUI web app and the implementation was carry out using Maven overlays. As of november November of 2017 it is supposed that members of the DSpace EGroup with ID 1 are administrators, members of EGroup 2 are experts and members of EGroup 3 are users, and this is hardcoded. SQL scripts to extend DSpace relational datamodel are given under loaDbScripts/, those should be executed for this customization to work propoerly. First shoud be run the proper DDL script, postgres or oracle, follow by the DML script. 

A refactor to the code in addtions module was made in order to apply a command design pattern to administrative assessments and to use DAOs (Data access objects) to make CRUD operations to the database. On the other hand, the code in the JSPUI module, needs to be refactor, there are some pretty ugly things, like switches constructs to manage several request inside the same method on a given servlet.

More work needs to be done, but the fundamental expected features described in the base model have been integrated to DSpace through this customization. 
