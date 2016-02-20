# Hacked together Delta Robot

Three cheap servo's, some metal bars and bunch of cable ties and glue.
Add an Arduino, PC, some math and a bunch of code and it's alive!

[![Robot in action](https://img.youtube.com/vi/Kn3VV5BHonI/0.jpg)](https://www.youtube.com/watch?v=Kn3VV5BHonI "Delta robot in action")

Here is an interface using Processing and Toxic Libs for volumetric
rendering, sadly I have no recording of the *arm-each-space* of the thing,
it's pretty cool! Maybe one day when I get this code running again.

[![Interface in action](https://img.youtube.com/vi/HjgTAZnQrv4/0.jpg)](https://www.youtube.com/watch?v=HjgTAZnQrv4 "Delta robot in action")

The arduino code is still burried somewhere... shouldn't be a big deal
to write again, it wasn't that complicated anyway. IIRC all it did was
maintain a ringbuffer of timed instructions and update the servo's
accordingly. That code was quite robust and reusable actually. :-/
