## ThoughtBook — Backlog & Notes

### Known Issues

* **`BookDetailActivity.updatePaceDisplay()`**

    * Currently creates a second Firestore listener even though the data is already being observed.
    * Not causing visible problems, but should be changed to use the existing list of entries instead of observing again.

* **Google Books search**

    * First search sometimes returns nothing, while the second identical search works.
    * Two identical API requests were also seen for a single search.
    * Need to check whether the click listener is being attached multiple times or if there's another duplicate request issue.

* **Cover image URLs**

    * Google Books returns `http://` image URLs.
    * May cause loading issues on devices that block cleartext traffic.
    * Convert URLs to `https://` before saving or configure network security if needed.

* **Google Books API key**

    * API key was shared during development.
    * Regenerate it, move it back to `local.properties`, and add package/SHA-1 restrictions before release.

* **Model classes**

    * Check that `EmotionLabel`, `ReadingLogEntry`, and `Shelf` have complete getters and setters like `Book`.

* **Image Parsing**
* The known issues on your backlog file — duplicate search listener, http/https cover URLs, duplicate Firestore listener in pace display — worth a pass if time allows, not blocking
---

## Future Features

* Recent search history on the Add Book screen.
* Profile page with reading statistics (books read, streak, favorite emotion, etc.).
* Better bookshelf organization (sorting and grouping options).
* Cozy bookshelf theme and dark/light mode.
* Custom emotion labels and colors.

---

