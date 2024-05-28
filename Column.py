class Column:
    # Constructor
        def __init__(self, tableName,name,type,nullable,primaryKey,datoType,datoImport,dataPrecision):
                self.tableName = tableName
                self.name = name
                self.type = type
                self.nullable = nullable
                if primaryKey == None:
                    primaryKey = ""
                self.primaryKey = primaryKey
                self.datoType = datoType
                self.datoImport = datoImport
                self.dataPrecision = dataPrecision

        def get(self):
            return self;        