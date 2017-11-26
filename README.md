# loa - Learning Objects Assessment

Customization for assess learning objects stored in DSpace.

This  development was based on a model for the quality assessment of learning objects which gatered together several approaches and considered the viewpoint of three actors, namely, repository administrators, experts and users. The module was integreted to DSpace's JPSUI web app and the implementation was carry out using Maven overlays.

As of november November of 2017 it is supposed that members of the DSpace EGroup with ID 1 are administrators, members of EGroup 2 are experts and members of EGroup 3 are users. And this is hardcoded.

SQL scripts to extend DSpace relational datamodel are given under loaDbScripts/, those should be executed for this customization to work propoerly. First shoud be run the proper DDL script, postgres or oracle, follow by the DML script.
