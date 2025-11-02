If the "add filament" button is pressed, a new screen should open.
in the new screen, we can add filament meta data.

screen title: "add filament"
input fields:
- "vendor" (free text, 1 line)
- "color": add a simple color picker, if there something in material 3 sdk
- "size": listbox with the following choices: "500g", "1kg", "2kg"

Please consider and take into account the design document located at 
design/overview.md for all considerations and changes.

If there are new strings to add, always "externalize" them. add entries to the default
strings.xml, also for any other languages (f.e. strings.xml in directory values-de).

Always add short, concise, useful comments to updated code fragments.