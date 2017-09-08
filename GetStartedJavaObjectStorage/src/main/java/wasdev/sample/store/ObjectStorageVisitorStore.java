/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package wasdev.sample.store;

import java.io.File;
import java.sql.Statement;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.storage.object.SwiftAccount;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.storage.object.options.ObjectListOptions;
import org.openstack4j.openstack.OSFactory;

import com.google.gson.JsonObject;

import wasdev.sample.Visitor;

public class ObjectStorageVisitorStore implements VisitorStore {

	private static SwiftAccount objectStorageAccount = null;
	private static OSClientV3 os;
	
	public ObjectStorageVisitorStore() {
		objectStorageAccount = createClient();
	}

	public SwiftAccount getConnection() {
		return objectStorageAccount;
	}

	private static SwiftAccount createClient() {
		String url1;
		String userId;
		String password;
		String auth_url;
		String domain;
		String project = "";
		Identifier domainIdent;
		Identifier projectIdent;
		
		

		boolean dedicated = true;

		System.out.println("creating a client for Object Storage");
		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the
			// credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for ObjectStorage.
			JsonObject objectStorageCredentials = VCAPHelper.getCloudCredentials("object storage dedicated"); //dedicated
			if (objectStorageCredentials == null) {
				objectStorageCredentials = VCAPHelper.getCloudCredentials("object-storage"); //public
				if (objectStorageCredentials == null) {
					System.out.println("No Object-Storage service bound to this application");
					return null;
				} else {
					dedicated = false;
				}
			}

			

			System.out.println(objectStorageCredentials);

			if (dedicated) {
				userId = objectStorageCredentials.get("accessKeyID").toString().replaceAll("^\"|\"$", "");
				password = objectStorageCredentials.get("secretAccessKey").toString().replaceAll("^\"|\"$", "");
				auth_url = objectStorageCredentials.get("endpoint-url").toString(); 
				//removing "" and adding /v3
				auth_url = auth_url.replaceAll("^\"|\"$", "") + "/v3";
				domain = objectStorageCredentials.get("region").toString().replaceAll("^\"|\"$", "");
				domainIdent = Identifier.byName(domain);
			} else {
				//public
				System.out.println("got objectstorage in Public Bluemix");
				userId = objectStorageCredentials.get("userId").toString().replaceAll("^\"|\"$", "");
				password = objectStorageCredentials.get("password").toString().replaceAll("^\"|\"$", "");
				auth_url = objectStorageCredentials.get("auth_url").toString().replaceAll("^\"|\"$", "") + "/v3";
				domain = objectStorageCredentials.get("domainName").toString().replaceAll("^\"|\"$", "");
				project = objectStorageCredentials.get("project").toString().replaceAll("^\"|\"$", "");
				domainIdent = Identifier.byName(domain);
				projectIdent = Identifier.byName(project);
			}
			
			System.out.println("got objectstorage credentials from VCAP: " + auth_url);

			
		} else {
			System.out.println("Running locally. Looking for credentials in objectstorage.properties");
			url1 = VCAPHelper.getLocalProperties("objectstorage.properties").getProperty("objectstorage_url");

			if (url1 == null || url1.length() == 0) {
				System.out.println(
						"To use an object-storage, set the url in src/main/resources/objectstorage.properties");
				return null;
			}
			userId = url1.substring(8);
			password = url1.substring(8);
			auth_url = url1.substring(8);
			domain = url1.substring(8);
			domainIdent = Identifier.byName(domain);
			
		}

		try {
			System.out.println("Attempting connection to Object Storage");
			if (dedicated) {
				System.out.println("Attempting connection to Object Storage - Dedicated");
				os = OSFactory.builderV3().endpoint(auth_url).credentials(userId, password)
					.scopeToProject(domainIdent).useNonStrictSSLClient(false)
					.authenticate();
			} else {
				System.out.println("Attempting connection to Object Storage - Public");
				
				os = OSFactory.builderV3()
						 .endpoint(auth_url)
						 .credentials(userId, password)
						 .scopeToProject(Identifier.byName(project), domainIdent)
						 .authenticate();
			}
			SwiftAccount account = os.objectStorage().account().get();			
			System.out.println("Connected to Object Storage");

			return account;
		} catch (Exception e) {
			System.out.println("Unable to connect to Object Storage");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	/**
	 * not used for ObjectStore
	 */
	public Object getDB() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAll(String containerName) {
		List<? extends SwiftObject> objs = os.objectStorage().objects().list(containerName);
		// TODO Auto-generated method stub
		return objs;
	}

	@Override
	public int count() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Visitor get(String id) {

		// System.out.println("starting reading : " + name);
		Visitor vi = new Visitor();
		vi.set_id(id);
		String name = id;
		String containerName = "test-java-container";//id;
		try {
			List<? extends SwiftObject> objs = os.objectStorage().objects().list(containerName, ObjectListOptions.create()
                    .startsWith(name)
                    //.path("/art/digital")
                    // ...
                 );
			if (objs != null) {
				System.out.println("read objs: " + objs.size());
			}
			
			
			// System.out.println("select done ");
		} catch (Exception sqle) {
			System.out.println("Could not read");
		}
		return vi;
	}

	@Override
	public Visitor persist(Visitor vi) {
		String objectName = vi.get_id();
		String containerName = "test-java-container"; //vi.get_id();
		String fileName = "tst.txt"; //vi.get_id();
		//recreating os
		System.out.println("updating session to object store");
		createClient();
		
		// Simple
		os.objectStorage().containers().create(containerName);
			
		// Full control
		/**
		 * os.objectStorage().containers().create("myContainer", CreateUpdateContainerOptions.create()
		 
		                                                         .accessAnybodyRead()
		                                                         .accessWrite(acl)
		                                                         .metadata(myMeta)
		                                                         //...
		                                                       ); #### Updating a Container
		*/
		try {
			File file = new File(fileName);
			  
			//Create the file
			if (file.createNewFile()){
			System.out.println("File is created!");
			}else{
			System.out.println("File already exists.");
			}
			//File someFile = new File(fileName);
			
			String etag = os.objectStorage().objects().put(containerName, objectName, Payloads.create(file));
		} catch (Exception e) {
			System.out.println("Could not persist");
			e.printStackTrace();
		}
		return vi;
	}

	@Override
	public Visitor update(String id, Visitor vi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		String objectName = id;
		String containerName = "test-java-container";//id;
		os.objectStorage().objects().delete(containerName, objectName);
		System.out.print("deleted an object");
		os.objectStorage().containers().delete(containerName);
		System.out.println(" and the container deleted");
	}

	@Override
	public Collection<Visitor> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Visitor push(Visitor vi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Visitor pop(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Visitor testSets(Visitor vi) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
