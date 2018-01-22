package com.google.example.firestore.kotlin

import android.support.annotation.WorkerThread
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import java.util.concurrent.*

/**
 * Kotlin version of doc snippets.
 *
 * Note: this is a naive auto-translation of the Java code with a few edits, this is not intended
 *       as a good example of idiomatic Kotlin.
 */
class DocSnippets(val db: FirebaseFirestore) {

    private val TAG = "DocSnippets"

    private val EXECUTOR = ThreadPoolExecutor(2, 4,
            60, TimeUnit.SECONDS, LinkedBlockingQueue()
    )

    internal fun runAll() {
        Log.d(TAG, "================= BEGIN RUN ALL ===============")

        // Write example data
        exampleData()

        // Run all other methods
        addAdaLovelace()
        addAlanTuring()
        getAllUsers()
        listenForUsers()
        docReference()
        collectionReference()
        subcollectionReference()
        setDocument()
        dataTypes()
        addDocument()
        newDocument()
        updateDocument()
        updateDocumentNested()
        setFieldWithMerge()
        deleteDocument()
        transactions()
        transactionPromise()
        getDocument()
        listenToDocument()
        listenToDocumentLocal()
        getMultipleDocs()
        getAllDocs()
        listenToMultiple()
        listenToDiffs()
        listenState()
        detachListener()
        handleListenErrors()
        simpleQueries()
        compoundQueries()
        orderAndLimit()
        queryStartAtEndAt()

        // Run methods that should fail
        try {
            compoundQueriesInvalid()
        } catch (e: Exception) {
            Log.d(TAG, "compoundQueriesInvalid", e)
        }

        try {
            orderAndLimitInvalid()
        } catch (e: Exception) {
            Log.d(TAG, "orderAndLimitInvalid", e)
        }

    }


    internal fun deleteAll() {
        deleteCollection("cities")
        deleteCollection("users")
    }

    private fun deleteCollection(path: String) {
        deleteCollection(db.collection(path), 50, EXECUTOR)
    }

    private fun setup() {
        // [START get_firestore_instance]
        val db = FirebaseFirestore.getInstance()
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        // [END set_firestore_settings]
    }

    private fun addAdaLovelace() {
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        val user = HashMap<String, Any>()
        user.put("first", "Ada")
        user.put("last", "Lovelace")
        user.put("born", 1815)

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
        // [END add_ada_lovelace]
    }


    private fun addAlanTuring() {
        // [START add_alan_turing]
        // Create a new user with a first, middle, and last name
        val user = HashMap<String, Any>()
        user.put("first", "Alan")
        user.put("middle", "Mathison")
        user.put("last", "Turring")
        user.put("born", 1912)

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
        // [END add_alan_turing]
    }

    private fun getAllUsers() {
        // [START get_all_users]
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        Log.d(TAG, document.id + " => " + document.data)
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
        // [END get_all_users]
    }

    private fun listenForUsers() {
        // [START listen_for_users]
        // Listen for users born before 1900.
        //
        // You will get a first snapshot with the initial results and a new
        // snapshot each time there is a change in the results.
        db.collection("users")
            .whereLessThan("born", 1900)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

                Log.d(TAG, "Current users born before 1900: " + snapshots!!)
            }
            )
        // [END listen_for_users]
    }

    private fun docReference() {
        // [START doc_reference]
        val alovelaceDocumentRef = db.collection("users").document("alovelace")
        // [END doc_reference]
    }

    private fun collectionReference() {
        // [START collection_reference]
        val usersCollectionRef = db.collection("users")
        // [END collection_reference]
    }

    private fun subcollectionReference() {
        // [START subcollection_reference]
        val messageRef = db
            .collection("rooms").document("roomA")
            .collection("messages").document("message1")
        // [END subcollection_reference]
    }

    private fun docReferenceAlternate() {
        // [START doc_reference_alternate]
        val alovelaceDocumentRef = db.document("users/alovelace")
        // [END doc_reference_alternate]
    }

    // [START city_class]
    data class City(val name: String?,
                    val state: String?,
                    val country: String?,
                    val isCapital: Boolean?,
                    val population: Long?) {
        // [START_EXCLUDE]
        constructor() : this(null, null, null, null, null)
        // [END_EXCLUDE]
    }
    // [END city_class]

    private fun setDocument() {
        // [START set_document]
        val city = HashMap<String, Any>()
        city.put("name", "Los Angeles")
        city.put("state", "CA")
        city.put("country", "USA")

        db.collection("cities").document("LA")
            .set(city)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        // [END set_document]

        val data = HashMap<String, Any>()

        // [START set_with_id]
        db.collection("cities").document("new-city-id").set(data)
        // [END set_with_id]
    }

    private fun dataTypes() {
        // [START data_types]
        val docData = HashMap<String, Any?>()
        docData.put("stringExample", "Hello world!")
        docData.put("booleanExample", true)
        docData.put("numberExample", 3.14159265)
        docData.put("dateExample", Date())
        docData.put("listExample", Arrays.asList(1, 2, 3))
        docData.put("nullExample", null)

        val nestedData = HashMap<String, Any>()
        nestedData.put("a", 5)
        nestedData.put("b", true)

        docData.put("objectExample", nestedData)

        db.collection("data").document("one")
            .set(docData)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        // [END data_types]
    }

    private fun addCustomClass() {
        // [START add_custom_class]
        val city = City("Los Angeles", "CA", "USA", false, 5000000L)
        db.collection("cities").document("LA").set(city)
        // [END add_custom_class]
    }

    private fun addDocument() {
        // [START add_document]
        // Add a new document with a generated id.
        val data = HashMap<String, Any>()
        data.put("name", "Tokyo")
        data.put("country", "Japan")

        db.collection("cities")
            .add(data)
            .addOnSuccessListener { documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.id) }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
        // [END add_document]
    }

    private fun newDocument() {
        // [START new_document]
        val data = HashMap<String, Any>()

        val newCityRef = db.collection("cities").document()

        // Later...
        newCityRef.set(data)
        // [END new_document]
    }

    private fun updateDocument() {
        // [START update_document]
        val washingtonRef = db.collection("cities").document("DC")

        // Set the "isCapital" field of the city 'DC'
        washingtonRef
            .update("capital", true)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        // [END update_document]
    }

    private fun updateDocumentNested() {
        // [START update_document_nested]
        // Assume the document contains:
        // {
        //   name: "Frank",
        //   favorites: { food: "Pizza", color: "Blue", subject: "recess" }
        //   age: 12
        // }
        //
        // To update age and favorite color:
        db.collection("users").document("frank")
            .update(
                    "age", 13,
                    "favorites.color", "Red"
            )
        // [END update_document_nested]
    }

    private fun setFieldWithMerge() {
        // [START set_field_with_merge]
        // Update one field, creating the document if it does not already exist.
        val data = HashMap<String, Any>()
        data.put("capital", true)

        db.collection("cities").document("BJ")
            .set(data, SetOptions.merge())
        // [END set_field_with_merge]
    }

    private fun deleteDocument() {
        // [START delete_document]
        db.collection("cities").document("DC")
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        // [END delete_document]
    }

    private fun transactions() {
        // [START transactions]
        val sfDocRef = db.collection("cities").document("SF")

        db.runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)
            val newPopulation = snapshot.getDouble("population")!! + 1
            transaction.update(sfDocRef, "population", newPopulation)

            // Success
            null
        }.addOnSuccessListener { Log.d(TAG, "Transaction success!") }
            .addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }
        // [END transactions]
    }

    private fun transactionPromise() {
        // [START transaction_with_result]
        val sfDocRef = db.collection("cities").document("SF")

        db.runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)
            val newPopulation = snapshot.getDouble("population")!! + 1
            if (newPopulation <= 1000000) {
                transaction.update(sfDocRef, "population", newPopulation)
                newPopulation
            } else {
                throw FirebaseFirestoreException("Population too high",
                        FirebaseFirestoreException.Code.ABORTED
                )
            }
        }.addOnSuccessListener { result ->
            Log.d(TAG, "Transaction success: " + result!!)
        }.addOnFailureListener { e ->
            Log.w(TAG, "Transaction failure.", e)
        }
        // [END transaction_with_result]
    }

    private fun writeBatch() {
        // [START write_batch]
        // Get a new write batch
        val batch = db.batch()

        // Set the value of 'NYC'
        val nycRef = db.collection("cities").document("NYC")
        batch.set(nycRef, City())

        // Update the population of 'SF'
        val sfRef = db.collection("cities").document("SF")
        batch.update(sfRef, "population", 1000000L)

        // Delete the city 'LA'
        val laRef = db.collection("cities").document("LA")
        batch.delete(laRef)

        // Commit the batch
        batch.commit().addOnCompleteListener {
            // ...
        }
        // [END write_batch]
    }

    private fun getDocument() {
        // [START get_document]
        val docRef = db.collection("cities").document("SF")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: " + task.result.data)
                } else {
                    Log.d(TAG, "No such document")
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        }
        // [END get_document]
    }

    private fun customObjects() {
        // [START custom_objects]
        val docRef = db.collection("cities").document("BJ")
        docRef.get().addOnSuccessListener { documentSnapshot -> val city = documentSnapshot.toObject(City::class.java) }
        // [END custom_objects]
    }

    private fun listenToDocument() {
        // [START listen_document]
        val docRef = db.collection("cities").document("SF")
        docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            if (snapshot != null && snapshot!!.exists()) {
                Log.d(TAG, "Current data: " + snapshot!!.data)
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
        )
        // [END listen_document]
    }

    private fun listenToDocumentLocal() {
        // [START listen_document_local]
        val docRef = db.collection("cities").document("SF")
        docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            val source = if (snapshot != null && snapshot!!.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && snapshot!!.exists()) {
                Log.d(TAG, source + " data: " + snapshot!!.data)
            } else {
                Log.d(TAG, source + " data: null")
            }
        }
        )
        // [END listen_document_local]
    }

    private fun getMultipleDocs() {
        // [START get_multiple]
        db.collection("cities")
            .whereEqualTo("capital", true)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        Log.d(TAG, document.id + " => " + document.data)
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
        // [END get_multiple]
    }

    private fun getAllDocs() {
        // [START get_multiple_all]
        db.collection("cities")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        Log.d(TAG, document.id + " => " + document.data)
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
        // [END get_multiple_all]
    }

    private fun listenToMultiple() {
        // [START listen_multiple]
        db.collection("cities")
            .whereEqualTo("state", "CA")
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

                val cities = ArrayList<String>()
                for (doc in value!!) {
                    if (doc.get("name") != null) {
                        cities.add(doc.getString("name"))
                    }
                }
                Log.d(TAG, "Current cites in CA: " + cities)
            }
            )
        // [END listen_multiple]
    }

    private fun listenToDiffs() {
        // [START listen_diffs]
        db.collection("cities")
            .whereEqualTo("state", "CA")
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> Log.d(TAG, "New city: " + dc.document.data)
                        DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: " + dc.document.data)
                        DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: " + dc.document.data)
                    }
                }
            }
            )
        // [END listen_diffs]
    }

    private fun listenState() {
        // [START listen_state]
        db.collection("cities")
            .whereEqualTo("state", "CA")
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "New city: " + dc.document.data)
                    }
                }

                if (!snapshots!!.metadata.isFromCache) {
                    Log.d(TAG, "Got initial state.")
                }
            }
            )
        // [END listen_state]
    }

    private fun detachListener() {
        // [START detach_listener]
        val query = db.collection("cities")
        val registration = query.addSnapshotListener { snapshots, e ->
            // [START_EXCLUDE]
            // ...
        }

        // ...

        // Stop listening to changes
        registration.remove()
        // [END detach_listener]
    }

    private fun handleListenErrors() {
        // [START handle_listen_errors]
        db.collection("cities")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(snapshots: QuerySnapshot?,
                                     e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                        return
                    }

                    for (dc in snapshots!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            Log.d(TAG, "New city: " + dc.document.data)
                        }
                    }

                }
            }
            )
        // [END handle_listen_errors]
    }

    private fun exampleData() {
        // [START example_data]
        val cities = db.collection("cities")

        val data1 = HashMap<String, Any>()
        data1.put("name", "San Francisco")
        data1.put("state", "CA")
        data1.put("country", "USA")
        data1.put("capital", false)
        data1.put("population", 860000)
        cities.document("SF").set(data1)

        val data2 = HashMap<String, Any>()
        data2.put("name", "Los Angeles")
        data2.put("state", "CA")
        data2.put("country", "USA")
        data2.put("capital", false)
        data2.put("population", 3900000)
        cities.document("LA").set(data2)

        val data3 = HashMap<String, Any?>()
        data3.put("name", "Washington D.C.")
        data3.put("state", null)
        data3.put("country", "USA")
        data3.put("capital", true)
        data3.put("population", 680000)
        cities.document("DC").set(data3)

        val data4 = HashMap<String, Any?>()
        data4.put("name", "Tokyo")
        data4.put("state", null)
        data4.put("country", "Japan")
        data4.put("capital", true)
        data4.put("population", 9000000)
        cities.document("TOK").set(data4)

        val data5 = HashMap<String, Any?>()
        data5.put("name", "Beijing")
        data5.put("state", null)
        data5.put("country", "China")
        data5.put("capital", true)
        data5.put("population", 21500000)
        cities.document("BJ").set(data5)
        // [END example_data]
    }

    private fun simpleQueries() {
        // [START simple_queries]
        // Create a reference to the cities collection
        val citiesRef = db.collection("cities")

        // Create a query against the collection.
        val query = citiesRef.whereEqualTo("state", "CA")
        // [END simple_queries]

        // [START simple_query_capital]
        val capitalCities = db.collection("cities").whereEqualTo("capital", true)
        // [END simple_query_capital]

        // [START example_filters]
        citiesRef.whereEqualTo("state", "CA")
        citiesRef.whereLessThan("population", 100000)
        citiesRef.whereGreaterThanOrEqualTo("name", "San Francisco")
        // [END example_filters]
    }

    private fun compoundQueries() {
        val citiesRef = db.collection("cities")

        // [START chain_filters]
        citiesRef.whereEqualTo("state", "CO").whereEqualTo("name", "Denver")
        citiesRef.whereEqualTo("state", "CA").whereLessThan("population", 1000000)
        // [END chain_filters]

        // [START valid_range_filters]
        citiesRef.whereGreaterThanOrEqualTo("state", "CA")
            .whereLessThanOrEqualTo("state", "IN")
        citiesRef.whereEqualTo("state", "CA")
            .whereGreaterThan("population", 1000000)
        // [END valid_range_filters]
    }

    private fun compoundQueriesInvalid() {
        val citiesRef = db.collection("cities")

        // [START invalid_range_filters]
        citiesRef.whereGreaterThanOrEqualTo("state", "CA").whereGreaterThan("population", 100000)
        // [END invalid_range_filters]
    }

    private fun orderAndLimit() {
        val citiesRef = db.collection("cities")

        // [START order_and_limit]
        citiesRef.orderBy("name").limit(3)
        // [END order_and_limit]

        // [START order_and_limit_desc]
        citiesRef.orderBy("name", Query.Direction.DESCENDING).limit(3)
        // [END order_and_limit_desc]

        // [START order_by_multiple]
        citiesRef.orderBy("state").orderBy("population", Query.Direction.DESCENDING)
        // [END order_by_multiple]

        // [START filter_and_order]
        citiesRef.whereGreaterThan("population", 100000).orderBy("population").limit(2)
        // [END filter_and_order]

        // [START valid_filter_and_order]
        citiesRef.whereGreaterThan("population", 100000).orderBy("population")
        // [END valid_filter_and_order]
    }

    private fun orderAndLimitInvalid() {
        val citiesRef = db.collection("cities")

        // [START invalid_filter_and_order]
        citiesRef.whereGreaterThan("population", 100000).orderBy("country")
        // [END invalid_filter_and_order]
    }

    private fun queryStartAtEndAt() {
        // [START query_start_at_single]
        // Get all cities with a population >= 1,000,000, ordered by population,
        db.collection("cities")
            .orderBy("population")
            .startAt(1000000)
        // [END query_start_at_single]

        // [START query_end_at_single]
        // Get all cities with a population <= 1,000,000, ordered by population,
        db.collection("cities")
            .orderBy("population")
            .endAt(1000000)
        // [END query_end_at_single]

        // [START query_start_at_doc_snapshot]
        // Get the data for "San Francisco"
        db.collection("cities").document("SF")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    // Get all cities with a population bigger than San Francisco.
                    val biggerThanSf = db.collection("cities")
                        .orderBy("population")
                        .startAt(documentSnapshot)

                    // ...
                }
            }
            )
        // [END query_start_at_doc_snapshot]

        // [START query_pagination]
        // Construct query for first 25 cities, ordered by population
        val first = db.collection("cities")
            .orderBy("population")
            .limit(25)

        first.get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(documentSnapshots: QuerySnapshot) {
                    // ...

                    // Get the last visible document
                    val lastVisible = documentSnapshots.documents[documentSnapshots.size() - 1]

                    // Construct a new query starting at this document,
                    // get the next 25 cities.
                    val next = db.collection("cities")
                        .orderBy("population")
                        .startAfter(lastVisible)
                        .limit(25)

                    // Use the query for pagination
                    // ...
                }
            }
            )
        // [END query_pagination]

        // [START multi_cursor]
        // Will return all Springfields
        db.collection("cities")
            .orderBy("name")
            .orderBy("state")
            .startAt("Springfield")

        // Will return "Springfield, Missouri" and "Springfield, Wisconsin"
        db.collection("cities")
            .orderBy("name")
            .orderBy("state")
            .startAt("Springfield", "Missouri")
        // [END multi_cursor]
    }

    // [START delete_collection]
    /**
     * Delete all documents in a collection. Uses an Executor to perform work on a background
     * thread. This does *not* automatically discover and delete subcollections.
     */
    private fun deleteCollection(collection: CollectionReference,
                                 batchSize: Int,
                                 executor: Executor): Task<Void> {

        // Perform the delete operation on the provided Executor, which allows us to use
        // simpler synchronous logic without blocking the main thread.
        return Tasks.call(executor, object : Callable<Void> {
            @Throws(Exception::class)
            override fun call(): Void? {
                // Get the first batch of documents in the collection
                var query = collection.orderBy(FieldPath.documentId()).limit(batchSize.toLong())

                // Get a list of deleted documents
                var deleted = deleteQueryBatch(query)

                // While the deleted documents in the last batch indicate that there
                // may still be more documents in the collection, page down to the
                // next batch and delete again
                while (deleted.size >= batchSize) {
                    // Move the query cursor to start after the last doc in the batch
                    val last = deleted[deleted.size - 1]
                    query = collection.orderBy(FieldPath.documentId())
                        .startAfter(last.id)
                        .limit(batchSize.toLong())

                    deleted = deleteQueryBatch(query)
                }

                return null
            }
        }
        )

    }

    /**
     * Delete all results from a query in a single WriteBatch. Must be run on a worker thread
     * to avoid blocking/crashing the main thread.
     */
    @WorkerThread
    @Throws(Exception::class)
    private fun deleteQueryBatch(query: Query): List<DocumentSnapshot> {
        val querySnapshot = Tasks.await(query.get())

        val batch = query.firestore.batch()
        for (snapshot in querySnapshot) {
            batch.delete(snapshot.reference)
        }
        Tasks.await(batch.commit())

        return querySnapshot.documents
    }
    // [END delete_collection]

    private fun offlineListen(db: FirebaseFirestore) {
        // [START offline_listen]
        db.collection("cities").whereEqualTo("state", "CA")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?,
                                     e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Log.w(TAG, "Listen error", e)
                        return
                    }

                    for (change in querySnapshot!!.documentChanges) {
                        if (change.type == DocumentChange.Type.ADDED) {
                            Log.d(TAG, "New city:" + change.document.data)
                        }

                        val source = if (querySnapshot!!.metadata.isFromCache)
                            "local cache"
                        else
                            "server"
                        Log.d(TAG, "Data fetched from " + source)
                    }

                }
            }
            )
        // [END offline_listen]
    }


    // [START server_timestamp_annotation]
    inner class MyObject {

        var name: String? = null
        @ServerTimestamp
        var timestamp: Date? = null
    }
    // [END server_timestamp_annotation]

    private fun updateWithServerTimestamp() {
        // [START update_with_server_timestamp]
        val docRef = db.collection("objects").document("some-id")

        // Update the timestamp field with the value from the server
        val updates = HashMap<String, Any>()
        updates.put("timestamp", FieldValue.serverTimestamp())

        docRef.update(updates).addOnCompleteListener(object : OnCompleteListener<Void> {
            // [START_EXCLUDE]
            override fun onComplete(task: Task<Void>) {}
            // [START_EXCLUDE]
        }
        )
        // [END update_with_server_timestamp]
    }

    private fun updateDeleteField() {
        // [START update_delete_field]
        val docRef = db.collection("cities").document("BJ")

        // Remove the 'capital' field from the document
        val updates = HashMap<String, Any>()
        updates.put("capital", FieldValue.delete())

        docRef.update(updates).addOnCompleteListener(object : OnCompleteListener<Void> {
            // [START_EXCLUDE]
            override fun onComplete(task: Task<Void>) {}
            // [START_EXCLUDE]
        }
        )
        // [END update_delete_field]
    }
}