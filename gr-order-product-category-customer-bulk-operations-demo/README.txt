
https://www.ionos.fr/digitalguide/sites-internet/developpement-web/mongodb-create-index/#c499141
https://www.geeksforgeeks.org/mongodb-db-collection-createindex-method/

## Créez un index sur orderId pour accélérer les insertions et éviter les doublons:
use shop
db.orders.createIndex({ "orderId": 1 }, { unique: true });



db.orders.getIndexes();
db.orders.dropIndex ({ "orderId": 1 });
db.orders.dropIndexes();