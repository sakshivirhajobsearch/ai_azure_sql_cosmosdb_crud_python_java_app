package com.ai.azure.sql.cosmosdb.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AzureCosmosRepository {

	private final CosmosContainer container;

	public AzureCosmosRepository(Properties config) {
		String endpoint = config.getProperty("cosmos_uri");
		String key = config.getProperty("cosmos_key");
		String databaseName = config.getProperty("cosmos_database");
		String containerName = config.getProperty("cosmos_container");

		CosmosClient client = new CosmosClientBuilder().endpoint(endpoint).key(key)
				.consistencyLevel(ConsistencyLevel.EVENTUAL).buildClient();

		CosmosDatabase database = client.getDatabase(databaseName);
		container = database.getContainer(containerName);
	}

	// ✅ Fetch all users (names only)
	public List<String> fetchUsers() {
		List<String> users = new ArrayList<>();

		String sqlQuery = "SELECT c.id, c.name FROM c";
		CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

		CosmosPagedIterable<ObjectNode> response = container.queryItems(sqlQuery, options, ObjectNode.class);

		for (ObjectNode node : response) {
			if (node.has("name")) {
				users.add(node.get("id").asText() + " - " + node.get("name").asText());
			}
		}

		return users;
	}

	// ✅ Insert new user
	public void insertUser(String id, String name) {
		Map<String, Object> item = new HashMap<>();
		item.put("id", id);
		item.put("name", name);

		try {
			container.createItem(item, new PartitionKey(id), new CosmosItemRequestOptions());
			System.out.println("Inserted user with id: " + id);
		} catch (CosmosException e) {
			System.err.println("Insert failed: " + e.getMessage());
		}
	}

	// ✅ Update user (replace entire document)
	public void updateUser(String id, String newName) {
		try {
			Map<String, Object> item = new HashMap<>();
			item.put("id", id);
			item.put("name", newName);

			container.replaceItem(item, id, new PartitionKey(id), new CosmosItemRequestOptions());
			System.out.println("Updated user with id: " + id);
		} catch (CosmosException e) {
			System.err.println("Update failed: " + e.getMessage());
		}
	}

	// ✅ Delete user
	public void deleteUser(String id) {
		try {
			container.deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());
			System.out.println("Deleted user with id: " + id);
		} catch (CosmosException e) {
			System.err.println("Delete failed: " + e.getMessage());
		}
	}
}
