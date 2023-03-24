/**
 * Home manager
 */

(function() {
    if (sessionStorage.getItem("username") == null) {
        redirect("login.html");
        return;
    }

    var header,
    	modal,
        folderTree,
        documentDetails,
        username,
        dragTarget,
        page = new PageOrchestrator();

    window.addEventListener("load", () => {
        page.start();
        page.show();
    });

    function Header(title, go_back_button, logout_button, bin) {
        this.title = title;
        this.go_back_button = go_back_button;
        this.logout_button = logout_button;
        this.bin = bin;
        this.target = null;
        
        this.start = () => { 
	        this.go_back_button.addEventListener("click", () => {
	            documentDetails.hide();
	            disable(go_back_button);
	            this.target = null;
	            
	            folderTree.show(false);
	            header.show("TREE");
	        });
	
	        this.logout_button.addEventListener("click", () => {
				makeCall("GET", "Logout", null, (request) => {
					if (request.readyState !== XMLHttpRequest.DONE) return;
					
					if (request.status != 200) {
						window.alert(request.responseText);
						return;
					}
					
					sessionStorage.removeItem("username");
					redirect("login.html");
				});
			});
			
			this.bin.addEventListener("dragover", (event) => {
				event.preventDefault();
			});
			
			this.bin.addEventListener("drop", (event) => {
				event.preventDefault();
				
				if (dragTarget == null) {
					return;	
				}
				
				this.target = dragTarget;
				modal.show();
			});
		};
				
		this.handleModalInteraction = (confirmed) => {
			if (!confirmed) {
				target = null;
				return;
			}
			
			var paramString = `?contentType=${this.target.type}&id=${this.target.id}`;
			var url = "DeleteContent" + paramString;
			target = null;
			
			makeCall("POST", url, null, (request) => {
				if (request.readyState !== XMLHttpRequest.DONE) return;
				
				if (request.status != 200) {
					window.alert(request.responseText);
					return;
				}
				
				folderTree.show(true);
			});
		};
		
        this.setDocument = (jsonMessage) => {
            this.target = jsonMessage.document;
        };
        
        this.show = (state) => {
            document.title = `${username}'s Folders`;
            var titleText = `C:\\${username}\\`;
			
            switch (state) {
                case "TREE":
                    titleText += `>tree /F`;
                    break;
                case "DOC":
                    if (this.target == null) return;
                    
                    titleText += `${this.target.name}.${this.target.type}`;

                    enable(go_back_button);
                    break;
                case "MOVE":
                   	titleText += `>tree /F`;
                   	
                   	enable(bin);
                    break;
                default:
                    return;
            }
            
            titleText += '<b class="blinker">_</b>';
            this.title.innerHTML = titleText;
        };
        
        this.hideBin = () => {
			disable(bin);
		};
    }
    
    function Modal(modalBox, closeButton, contentName, confirmButton) {
		this.modalBox = modalBox;
		this.closeButton = closeButton;
		this.contentName = contentName;
		this.confirmButton = confirmButton;
		
		this.start = () => {
			this.closeButton.addEventListener("click", () => {
				modalBox.style.display = "none";
				header.handleModalInteraction(false);
			});
			
			this.confirmButton.addEventListener("click", () => {
				modalBox.style.display = "none";
				header.handleModalInteraction(true);
			});
			
			window.addEventListener("click", (event) => {
				if (event.target !== modalBox) return;
				
				modalBox.style.display = "none";
				header.handleModalInteraction(false);
			});
		};
		
		this.show = () => {
			modalBox.style.display = "block";
			contentName.textContent = `the ${dragTarget.type} ${dragTarget.name}`;
		};
	};
    
    function FolderTree(folderList) {
		this.folderList = folderList;
		
		this.show = (shouldFetch = true) => {
			if (!shouldFetch) {
				enable(this.folderList);
				return;
			}
			
			makeCall("GET", "GetFolderTree", null, (request) => {
				if (request.readyState !== XMLHttpRequest.DONE) return;
				
				var message = request.responseText;
				
				if (request.status != 200) {
					window.alert(message);
					return;
				}
				
				this.buildTree(JSON.parse(message));
				enable(this.folderList);
			});
		};
		
		this.buildTree = (treeJson) => {
			if (!treeJson.hasOwnProperty("folderTree")) return;
			var folders = treeJson.folderTree;
			
			folderList.innerHTML = "";
			
			if (folders.length == 0) {
				folderList.innerHTML = "You don't have any content yet. Create a folder:";
				var form = document.createElement("form");
				form.innerHTML = '<input name="contentType" value="FOLDER" class="disabled"/> ' +
					'name <i class="input-arrow arrow right"></i> <input placeholder="name" maxlength="30" name="name" required/>';
				
				var button = document.createElement("input");
				button.classList.add("button");
				button.type = "submit";
				button.value = "Create";
				button.addEventListener("click", (event) => {
					event.preventDefault();
					this.hadleCreateClick(event.target.closest("form"));
				});
				
				form.appendChild(button);
				folderList.appendChild(form);
				return;
			}
			
			folders.forEach((folder) => {
				var folderItem = document.createElement("li");
				
				folderItem.appendChild(this.buildListItem(folder, "folder-svg", "assets/folder.svg", null, this.hadleCreateClick));
				folderItem.appendChild(this.buildSubfolders(folder));
				
				folderList.appendChild(folderItem);
			});
		};
		
		this.buildSubfolders = (subfoldersJson) => {
			if (!subfoldersJson.hasOwnProperty("subfolders")) return;
			var subfolders = subfoldersJson.subfolders;
			
			var subfolderList = document.createElement("ul");
			
			subfolders.forEach((subfolder) => {
				var subfolderItem = document.createElement("li");
				subfolderItem.className = "subfolder";
				
				subfolderItem.appendChild(this.buildListItem(subfolder, "folder-svg", "assets/folder.svg", null, this.hadleCreateClick, true));
				subfolderItem.appendChild(this.buildDocuments(subfolder));
				
				subfolderItem.addEventListener("drop", (event) => {
					event.preventDefault();
					
					if (dragTarget.type !== "DOCUMENT") return; 
					
					var paramString = `?documentId=${dragTarget.id}&subfolderId=${subfolder.id}`;
					var url = "MoveDocument" + paramString;
					
					makeCall("GET", url, null, (request) => {
						if (request.readyState !== XMLHttpRequest.DONE) return;
	
						if (request.status != 200) {
							window.alert(request.responseText);
							return;
						}
							
						this.show(true);
					});
				});
				
				subfolderList.appendChild(subfolderItem);
			});
			
			return subfolderList;
		};
		
		this.buildDocuments = (documentsJson) => {
			if (!documentsJson.hasOwnProperty("documents")) return;
			var documents = documentsJson.documents;
			
			var documentList = document.createElement("ul");
			
			documents.forEach((doc) => {
				var documentItem = document.createElement("li");
				
				documentItem.appendChild(
					this.buildListItem(doc, "document-png", "assets/document.png",
						(documentId) => {
							documentDetails.show(documentId);
							
							this.hide();
						}
					)
				);
				
				documentList.appendChild(documentItem);
			});
			
			return documentList;
		};
		
		this.buildListItem = (json, iconClass, iconSource, onNameClick = null, onFormClick = null, isDocumentForm = false) => {
			var div = document.createElement("div");
			div.classList.add("folder");
			div.draggable = true;
			
			var icon = document.createElement("img");
			icon.classList.add(iconClass);
			icon.src = iconSource;
			div.appendChild(icon);
			
			var name;
			
			if (onNameClick == null) {
				name = document.createElement("span");
				
				div.addEventListener("dragend", () => {
					this.handleDragEnd();
				});
			} else {
				name = document.createElement("a");
				name.addEventListener("click", () => {
					onNameClick(json.id);	
				});
				
				
				div.addEventListener("dragstart", (event) => {
					for(subfolderDiv of document.getElementsByClassName("subfolder")) {
						subfolderDiv.addEventListener("dragover", this.preventDefault);
					};
					
					var thisDiv = event.target.closest(".subfolder");
					thisDiv.classList.add("deselected");
					thisDiv.removeEventListener("dragover", this.preventDefault);
					
					this.handleDragStart("DOCUMENT", json);
				});
				
				div.addEventListener("dragend", () => {
					for(subfolderDiv of document.getElementsByClassName("subfolder")) {
						subfolderDiv.removeEventListener("dragover", this.preventDefault);
						subfolderDiv.classList.remove("deselected");
					}
					
					this.handleDragEnd();
				});
			}
			
			name.textContent = json.name;
			div.appendChild(name);
			
			if (onFormClick != null) {
				var addItem = document.createElement("a");
				var formItem = document.createElement("form");
				
				addItem.classList.add("button");
				addItem.textContent = "+";
				addItem.classList.add("add-button");
				addItem.addEventListener("click", (event) => {
					var target = event.target.closest(".add-button");
					
					if (target.textContent === "+") {
						target.innerHTML = "&#8722;";
						enable(formItem);
					} else {
						target.textContent = "+";
						disable(formItem);
					}
				})
				
				var innerHTML = 'name> <input placeholder="name" maxlength="30" name="name" required/>';
				
				if (isDocumentForm) {
					innerHTML += 'summary> <input placeholder="summary" name="summary" required/>';
					innerHTML += 'type> <input placeholder="type" maxlength="3" name="type" required/>';
					innerHTML += '<input name="contentType" value="DOCUMENT" class="disabled"/>';
					innerHTML += `<input name="parentSubfolderId" value="${json.id}" class="disabled"/>`;
					
					div.addEventListener("dragstart", () => {
						this.handleDragStart("SUBFOLDER", json);
					});
				} else {
					innerHTML += '<input name="contentType" value="SUBFOLDER" class="disabled"/>';
					innerHTML += `<input name="parentFolderId" value="${json.id}" class="disabled"/>`;
					
					div.addEventListener("dragstart", () => {
						this.handleDragStart("FOLDER", json);
					});
				}
				
				formItem.innerHTML = innerHTML;
				formItem.classList.add("inline-form");
				formItem.classList.add("h-container");
				
				var formButton = document.createElement("input");
				formButton.classList.add("button");
				formButton.value = "Create";
				formButton.type = "submit";
				formButton.addEventListener("click", (event) => {
					event.preventDefault();
					onFormClick(event.target.closest("form"), () => {
						enable(addItem);
						disable(formItem);
					});
				});
				formItem.appendChild(formButton);
				formItem.classList.add("disabled");
				
				div.appendChild(addItem);
				div.appendChild(formItem);
			}
			
			var date = document.createElement("span");
			date.classList.add("date");
			date.innerHTML = `creation date <i class="input-arrow arrow right"></i><span>${json.creationDate}</span>`;
			div.appendChild(date);
		
			return div;
		};
		
		this.hadleCreateClick = (form, callback = null) => {
			if (form == null) return;
			
			if (!form.checkValidity()) {
				form.reportValidity();
				return;
			}
			
			makeCall("POST", "CreateContent", form, (request) => {
				if (request.readyState !== XMLHttpRequest.DONE) return;
	
				if (request.status != 200) {
					window.alert(request.responseText);
					return;
				}
				
				if (callback != null)
					callback();
					
				this.show(true);
			});
		};
		
		this.handleDragStart = (type, json) => {
			dragTarget = {type: type, id: json.id, name: json.name};
			header.show("MOVE");
		};
		
		this.handleDragEnd = () => {
			dragTarget = null;
			header.hideBin();
			header.show("TREE");
		};
		
		this.preventDefault = (event) => {
			event.preventDefault();
		};
		
		this.hide = () => {
			disable(this.folderList);
		};
	}
    
    function DocumentDetails(detailsDiv, name, date, summary, type, owner) {
		this.detailsDiv = detailsDiv;
		this.name = name;
		this.date = date;
		this.summary = summary;
		this.type = type;
		this.owner = owner;
        
		this.show = (documentId) => {
			if (Number.isNaN(documentId)) return;
			
			var paramString = `?documentId=${documentId}`;
			var url = "GetDocumentDetails" + paramString;
			
			makeCall("GET", url, null, (request) => {
				if (request.readyState !== XMLHttpRequest.DONE) return;
				
				var message = request.responseText;

				if (request.status != 200) {
					window.alert(message);
					return;
				}
				
				var jsonMessage = JSON.parse(message);

				this.buildDocumentDetails(jsonMessage);

				enable(this.detailsDiv);
				
				header.setDocument(jsonMessage);
				header.show("DOC");
			});
		};
		
		this.hide = () => {
			disable(this.detailsDiv);
			
		};
		
		this.buildDocumentDetails = (jsonMessage) => {
			if (jsonMessage == null || !jsonMessage.hasOwnProperty("document")) return;
			var document = jsonMessage.document;

			this.name.textContent = document.name;
			this.date.textContent = document.creationDate;
			this.summary.textContent = document.summary;
			this.type.textContent = document.type;
			this.owner.textContent = username;
		};
	}

    function PageOrchestrator() {
        this.start = () => {
			username = sessionStorage.getItem("username");
	
            header = new Header(
                document.getElementById("title"),
                document.getElementById("go_back_button"),
                document.getElementById("logout_button"),
                document.getElementById("bin")
            );
            header.start();
            
            modal = new Modal(
				document.getElementById("modal"),
				document.getElementById("close-button"),
				document.getElementById("content-name"),
				document.getElementById("confirm-button")
			);
            modal.start();
            
            folderTree = new FolderTree(
				document.getElementById("folderTree")
            );
            
            documentDetails = new DocumentDetails(
				document.getElementById("document_details"),
				document.getElementById("document_name"),
				document.getElementById("document_date"),
				document.getElementById("document_summary"),
				document.getElementById("document_type"),
				document.getElementById("document_owner")
			);
        };

        this.show = () => {		
            header.show("TREE");
            folderTree.show();
        };
    }
})();