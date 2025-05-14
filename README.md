# gr-mongodb-module-demo
docker run -d --name mongodb \
	-e MONGO_INITDB_ROOT_USERNAME=admin \
	-e MONGO_INITDB_ROOT_PASSWORD=admin \
	-p 27017:27017 \
	-v mongodb_data:/opt/mongodb \
	mongo
--------------------------------------------------------------------------
--------------------------------------------------------------------------
docker network ls
docker network inspect <network_name>

docker network connect <network_name> mongodb
docker network connect <network_name> <springboot_container>

docker volume rm mongodb_data
--------------------------------------------------------------------------
--------------------------------------------------------------------------
docker exec mongodb mongod --version
--------------------------------------------------------------------------
docker exec -it mongodb mongosh -u admin -p admin
--------------------------------------------------------------------------
https://www.mongodb.com/docs/manual/
https://www.geeksforgeeks.org/mongodb-tutorial/

show dbs
use shop
db
show collections

db.nom_collection.insertOne({ nom: "Jihed", age: 35 })
db.nom_collection.insertMany([{ champ: "valeur1" }, { champ: "valeur2" }])

db.dropDatabase()
db.createCollection("nom_collection") 	// Créer une collection explicitement (facultatif, car les collections sont créées automatiquement)
db.nom_collection.drop()

db.categories.count();
db.categories.find()
db.categories.find().pretty()
db.categories.find({ categoryName: 'Electronics' })

db.categories.updateOne({ categoryName: 'Electronics' }, { $set: { categoryName: "Electronics!" } })
-------
db.categories.deleteOne({ categoryName: 'Electronics' })
db.categories.deleteMany({ categoryName: 'Electronics' })
db.categories.remove({})		// delete all documents
db.categories.deleteMany({})	// delete all documents

db.getCollectionNames().forEach(
	function(collectionName) { 
		if (collectionName.indexOf("system.") === -1) {
			print ( ["Removing: ", db[collectionName].count({}), " documents from ", collectionName].join('') );
			db[collectionName].remove({});
		}
	}
);

db.getCollectionNames()
	.filter((collectionName) => collectionName.indexOf("system.") === -1)
	.forEach(
		(collectionName) => {
			print(`Removing: ${db[collectionName].count({})} documents from ${collectionName}`);
			db[collectionName].deleteMany({});
		}
	);
--------------------------------------------------------------------------
use admin
show collections
		system.users
		system.version

db.system.users.find();

db.auth("admin", "admin")
db.createUser({
    user: "admin",
    pwd: "admin",
    roles: [{ role: "root", db: "admin" }]
})


