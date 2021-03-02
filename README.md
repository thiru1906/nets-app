# news-app
News app is a sample project to understand RESTful API
and create Java entity beans based on the json configuration provided in webapps/app_configuration.
The core does the following : 
<ul><li>maps the entities with its supported APIs</li>
<li>supports a transformer that can be used to transform data of all fields to/from database (in roadmap)</li> </ul>

To create a new table/entity, put a json in app_configuration 
```
{
	"name" : "internal_publisher",
	"api_path" : "/internal_publishers",
	"table_name" : "internal_publisher",
	"handler" : "InternalPublisherHandler",
	"attributes" : [
		{
			"name" : "id",
			"display_name" : "publisher_id",
			"column_name" : "internal_publisher.id",
			"type" : "long",
			"ref_display" : true,
			"identifier" : true
		},
		{
			"name" : "email_id",
			"column_name" : "internal_publisher.email_id",
			"display_name" : "email_id",
			"ref_display" : true,
			"type" : "string"
		}
	
	],
	"supported_operations" : [
		"add", "edit", "delete", "get"
	]
}
```
This sample configuration gives us a overall idea about how the Java bean is mapped with schema table and API
