<template>
  <div>
    <header>
      <h1>MY BOOKSHELF 2025</h1>
    </header>
    <div class="goal-container">
    <div>
      <label for="goalInput">Objetivo de libros leídos:</label>
      <input
        type="number"
        v-model="goalInput"
        placeholder="Introduce un número"
        :disabled="goal > 0 && !editingGoal"
        class="small-input"
      />
      <button class="goal-button"
        @click="setGoal"
        :disabled="goalInput <= 0"
        v-if="!goal || editingGoal"
      >
        Marcar objetivo
      </button>
      <button class="goal-button"
        @click="editGoal"
        v-else
      >
        Cambiar objetivo
      </button>
    </div>

    <div class="books-read">
      <p>Libros leídos: {{ books.length }}</p>
    </div>

    <div class="progress-container">
      <p>Progreso: {{ progressPercentage }}%</p>
      <div class="progress-bar">
        <div class="progress" :style="{ width: progressPercentage + '%' }"></div>
      </div>
    </div>
    </div>
    <div class="book-container">
      <div class="book" v-for="book in books" :key="book.id" :style="{ backgroundImage: `url(${book.imageUrl})` }">
      <div class="book-content">
        <h3>{{ book.title }}</h3>
        <p><strong>Autor:</strong> {{ book.author }}</p>
        <p><strong>Género:</strong> {{ book.genre }}</p>
        <p><strong>Fecha de lectura:</strong> {{ book.readingDate }}</p>
        <p class="rating">⭐ {{ book.rating }}</p>
        <button @click="startEdit(book)" class="update-button">Actualizar</button>
      </div>
    </div>
    </div>
    <div class="export-import-container">
      <div v-if="books.length > 0" class="dropdown">
        <button class="export-button" @click="toggleDropdown">
          Exportar
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="dropdown-icon">
            <path d="M12 16l-6-6h4V4h4v6h4l-6 6zM5 18h14v2H5z" />
          </svg>
        </button>
        <div v-if="showDropdown" class="dropdown-menu">
          <button @click="exportBooks('json')" class="dropdown-item">Exportar JSON</button>
          <button @click="exportBooks('csv')" class="dropdown-item">Exportar CSV</button>
        </div>
      </div>
    </div>
    <div class="form-container">
      <div class="add-book-form">
        <h2>Añadir un nuevo libro</h2>
        <form @submit.prevent="addBook">
          <input type="text" v-model="newBook.title" placeholder="Título" required style="font-size: 16px;" />
          <input type="text" v-model="newBook.author" placeholder="Autor" required style="font-size: 16px;"/>
          <input type="text" v-model="newBook.genre" placeholder="Género" required style="font-size: 16px;"/>
          <input type="date" v-model="newBook.readingDate" placeholder="Fecha de lectura" required style="font-size: 16px;"/>
          <input type="number" v-model="newBook.rating" placeholder="Nota" min="1" max="5" required style="font-size: 16px;"/>
          <button type="button" @click="showImageModal = true">Añadir imagen</button>
          <div v-if="showImageModal" class="modal-overlay">
            <div class="modal-content">
              <h3>Introducir URL de imagen</h3>
              <input type="text" v-model="tempImageUrl" placeholder="https://..." class="edit-input" />
              <button @click="addImageToBook">Añadir imagen</button>
              <button @click="showImageModal = false">Cerrar</button>
            </div>
          </div>
          <button type="submit" class="submit-button">¡Leído!</button>
        </form>
      </div>
      <div class="delete-book-form">
        <h2>Eliminar un libro</h2>
        <form @submit.prevent="confirmDeleteBook">
          <select v-model="bookToDelete" required>
            <option disabled value="">Selecciona un libro</option>
            <option v-for="book in books" :key="book.id" :value="book.id">{{ book.title }}</option>
          </select>
          <button type="submit" class="delete-button">Eliminar</button>
        </form>
      </div>
    </div>
    <div v-if="isEditing" class="modal-overlay">
      <div class="modal">
        <h2>Editar libro</h2>
        <form @submit.prevent="updateBook">
          <input type="text" v-model="bookToEdit.title" placeholder="Título" required />
          <input type="text" v-model="bookToEdit.author" placeholder="Autor" required />
          <input type="text" v-model="bookToEdit.genre" placeholder="Género" required />
          <input type="date" v-model="bookToEdit.readingDate" placeholder="Fecha de lectura" required />
          <input type="number" v-model="bookToEdit.rating" placeholder="Nota" min="1" max="5" required />
          <button type="button" @click="showImageModal = true" class="add-image-button">
          Añadir imagen
        </button>
        <!-- Modal para introducir URL -->
        <div v-if="showImageModal" class="modal-overlay">
          <div class="modal-content">
            <h4>Introduce la URL de la imagen</h4>
            <input
              type="text"
              v-model="newImageUrl"
              placeholder="https://..."
            />
            <div class="modal-buttons">
              <button type="button" @click="addImageUrl">Añadir imagen</button>
              <button type="button" @click="showImageModal = false">Cerrar</button>
            </div>
          </div>
        </div>
          <button type="submit" class="submit-button">Actualizar libro</button>
          <button type="button" class="close-button" @click="closeModal">Cerrar</button>
        </form>
      </div>
    </div>
    <div v-if="confirmationMessage" :class="['confirmation-message', confirmationType === 'error' ? 'error' : 'success']">
      <svg
        v-if="confirmationType === 'success' || confirmationType === 'error'"
          class="icon-check"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 512 512"
          aria-hidden="true"
          focusable="false"
        >
        <path
          fill="currentColor"
          d="M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM369 209L241 337c-9.4 9.4-24.6 9.4-33.9 0l-64-64c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0l47 47L335 175c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9z"
        ></path>
      </svg>
      {{ confirmationMessage }}
    </div>
    <div v-if="showDeleteConfirm" class="confirmation-message" style="background-color: grey;">
      ¿Está seguro de que desea eliminar el libro?
      <div>
        <button @click="deleteBook" class="delete-button" style="background-color: #e74c3c;">Sí, eliminar</button>
        <button @click="cancelDelete" class="delete-button" style="background-color: black;">Cancelar</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "BookList",
  data() {
    return {
      books: [],  // Lista de libros
      newBook: {  // Datos del nuevo libro
        title: "",
        author: "",
        genre: "",
        readingDate: "",
        rating: null,
        imageUrl: "",
      },
      bookToDelete: "", // ID del libro a eliminar
      bookToEdit: null, // Datos del libro que se va a editar
      isEditing: false, // Estado para mostrar el formulario de edición
      confirmationMessage: "", // Mensaje de confirmación  
      confirmationType: "", 
      showDeleteConfirm: false,     
      goal: 0, 
      goalInput: "", 
      editingGoal: false, 
      showImageModal: false,
      tempImageUrl: "",
      newImageUrl: "",
      showDropdown: false, // Estado para mostrar/ocultar el menú desplegable
    };
  },
  mounted() {
    this.fetchBooks();
    const savedGoal = localStorage.getItem("readingGoal");
    if (savedGoal) {
      this.goal = parseInt(savedGoal);
      this.goalInput = savedGoal;
    }  
    const savedImageUrl = localStorage.getItem('bookImage');
    if (savedImageUrl) {
      // Si hay una imagen guardada, asignamos su URL al atributo imageUrl de los libros
      this.books.forEach(book => {
        if (book.imageUrl === "") {  // Si un libro no tiene imagen, lo asignamos
          book.imageUrl = savedImageUrl;
        }
      });
    }
},
  computed: {
    progressPercentage() {
    if (this.goal === 0 || this.books.length === 0) {
      return 0;  // Si no hay objetivo o no hay libros leídos, el progreso es 0
    }
    let percentage = (this.books.length / this.goal) * 100;
    return Math.min(percentage, 100).toFixed(2);  // Limita al 100% como máximo
  },
  },
  methods: {
  addImageUrl() {
    if (this.bookToEdit) {
      this.bookToEdit.imageUrl = this.newImageUrl;
      this.showImageModal = false;
      this.newImageUrl = "";
    }
  },
  addImageToBook() {
      this.newBook.imageUrl = this.tempImageUrl;
      
      // Guardar la imagen para este libro específico en localStorage
      const booksFromStorage = JSON.parse(localStorage.getItem('books')) || [];
      booksFromStorage.push(this.newBook);
      localStorage.setItem('books', JSON.stringify(booksFromStorage));

      this.showImageModal = false;
      this.tempImageUrl = "";
    },
    setGoal() {
      if (this.goalInput && !isNaN(this.goalInput)) {
      this.goal = parseInt(this.goalInput);
      localStorage.setItem("readingGoal", this.goal);  // Guardar en localStorage
      this.editingGoal = false; 
    }
  },
    editGoal() {
      this.editingGoal = true;
    },
    fetchBooks() {
    fetch("http://localhost:8080/api/bookshelf")
      .then((res) => res.json())
      .then((dataFromApi) => {
        const booksFromStorage = JSON.parse(localStorage.getItem("books")) || [];

        // Fusionar datos del servidor con los del localStorage (especialmente imageUrl)
        const mergedBooks = dataFromApi.map(book => {
          const localBook = booksFromStorage.find(b => b.id === book.id);
          return localBook ? { ...book, imageUrl: localBook.imageUrl || book.imageUrl } : book;
        });

        this.books = mergedBooks;

        // También actualizamos el localStorage para mantenerlo sincronizado
        localStorage.setItem("books", JSON.stringify(mergedBooks));
      })
      .catch((err) => console.error("Error:", err));
  },
  addBook() {
  fetch("http://localhost:8080/api/bookshelf", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(this.newBook),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error al guardar el libro");
      }
      this.fetchBooks();  // Actualizar la lista de libros
      this.confirmationMessage = "¡Libro añadido!";
      this.confirmationType = "success";
      setTimeout(() => {
        this.confirmationMessage = "";
      }, 3000);
      this.newBook = {
        title: "",
        author: "",
        genre: "",
        readingDate: "",
        rating: null,
      };
    })
    .catch((err) => console.error("Error:", err));
  },
  startEdit(book) {
    this.bookToEdit = { ...book };
    this.isEditing = true;
  },
  updateBook() {
  fetch(`http://localhost:8080/api/bookshelf/${this.bookToEdit.id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(this.bookToEdit),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error al actualizar el libro");
      }

      // Actualizar el libro en el estado de Vue.js
      const index = this.books.findIndex((book) => book.id === this.bookToEdit.id);
      this.books[index] = this.bookToEdit;

      // Actualizar el libro en el localStorage
      const booksFromStorage = JSON.parse(localStorage.getItem('books')) || [];
      const storageIndex = booksFromStorage.findIndex((book) => book.id === this.bookToEdit.id);

      if (storageIndex !== -1) {
        booksFromStorage[storageIndex] = this.bookToEdit; // Actualiza la referencia del libro en el localStorage
        localStorage.setItem('books', JSON.stringify(booksFromStorage)); // Guarda los cambios en localStorage
      }

      // Cerrar el modal
      this.closeModal();

      // Mostrar el mensaje de confirmación
      this.confirmationMessage = "Libro actualizado correctamente";
      this.confirmationType = "success";
      setTimeout(() => {
        this.confirmationMessage = "";
      }, 3000);
    })
    .catch((err) => {
      console.error("Error:", err);
      this.confirmationMessage = "Error al actualizar el libro";
      this.confirmationType = "error";
      setTimeout(() => {
        this.confirmationMessage = "";
      }, 3000);
    });
},
    closeModal() {
      this.isEditing = false;
      this.bookToEdit = null;
    },
    confirmDeleteBook() {
        if (!this.bookToDelete) {
          return;
        }
        this.showDeleteConfirm = true;  // Mostrar confirmación de eliminación
      },
    deleteBook() {
      if (!this.bookToDelete) {
        return;
      }
      fetch(`http://localhost:8080/api/bookshelf/${this.bookToDelete}`, {
        method: "DELETE",
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Error al eliminar el libro");
          }
          this.books = this.books.filter((book) => book.id !== this.bookToDelete);
          this.bookToDelete = "";
          this.confirmationMessage = "Libro eliminado";
          this.confirmationType = "error";
          setTimeout(() => {
            this.confirmationMessage = "";
          }, 3000);
          this.showDeleteConfirm = false;  // Ocultar la confirmación
        })
        .catch((err) => console.error("Error:", err));
    },
    cancelDelete() {
      this.showDeleteConfirm = false;  // Ocultar la confirmación
    },
    toggleDropdown() {
      this.showDropdown = !this.showDropdown; // Alternar visibilidad del menú
    },
    exportBooks(format) {
      fetch(`http://localhost:8080/api/bookshelf/export?format=${format}`)
        .then((response) => {
          if (!response.ok) {
            throw new Error("Error al exportar los libros");
          }
          return response.text();
        })
        .then((data) => {
          const blob = new Blob([data], { type: format === "json" ? "application/json" : "text/csv" });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement("a");
          link.href = url;
          link.download = `books.${format}`;
          link.click();
          window.URL.revokeObjectURL(url);
        })
        .catch((err) => console.error("Error:", err));
    },
  }
};
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap');

body {
  font-family: 'Poppins', sans-serif;
  margin: 0;
  padding: 0;
  min-height: 100vh; 
  display: flex;
  flex-direction: column; 
  justify-content: flex-start; 
  background-color: #f4f4f4;
}

header {
  top: 0;
  left: 0;
  width: 100%;
  height: 200px;
  background-image: url('https://www.shutterstock.com/image-photo/banner-hardcover-books-row-isolated-600nw-2123120615.jpg');
  background-position: center;
  color: white;
  text-align: center;
  z-index: 1000;
}

header h1 {
  margin: 0;
  padding-top: 60px; 
  color: black;
}

.book-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  padding-top: 40px;
  width: 100%; 
}

.book {
  display: flex;
  flex-direction: column;
  background-color: rgba(255, 255, 255, 0.8);  /* Fondo blanco semitransparente */
  border-radius: 8px;
  width: 250px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease-in-out;
  height: 260px;
  background-size: cover;
  background-position: center;
  position: relative;  /* Necesario para posicionar el contenido por encima de la imagen */
}

.book:hover {
  transform: scale(1.05);
}

.book h3 {
  margin: 10px 0 5px;
  font-size: 1.2em;
  color: #333;
  z-index: 2;  /* Se asegura de que el texto esté encima de la imagen */
}

.book p {
  font-size: 0.9em;
  color: black;
  margin: 5px 0;
  z-index: 2;  /* Se asegura de que el texto esté encima de la imagen */
}

.rating {
  font-size: 1.2em;
  color: #f39c12;
}

.book-content {
  position: relative;  /* Para que los elementos dentro del libro se ubiquen sobre la imagen */
  z-index: 1;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 5px;
} /*El div book-content quede uniforme, ocupe el mismo espacio*/

.book img.book-image {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 10px;
}

.book::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7); /* Fondo blanquecino con opacidad */
  border-radius: 8px;
  z-index: 0;  /* Asegura que el fondo esté debajo de la información */
}

.form-container {
  display: flex;
  gap: 40px; 
  justify-content: center;
  margin-top: 40px;
}

.add-book-form {
  background-color: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  text-align: center;
  margin-bottom: 40px;
}

.add-book-form h2 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
}

.delete-book-form {
  background-color: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  text-align: center;
  margin-bottom: 40px;
}

.delete-book-form h2 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
}

.delete-book-form select {
  width: 100%;
  padding: 12px;
  margin-top: 5px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1em;
}

.delete-button {
  background-color: #e23134;
  width: 40%;
  padding: 12px;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px;
}

.delete-button:hover {
  background-color: #c31d20;
}

.submit-button {
  width: 40%;
  padding: 12px;
  background-color: black;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: grey;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* Fondo semi-transparente */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 500px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.modal h2 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
  text-align: center;
}

.modal input {
  width: 100%;
  padding: 12px;
  margin-top: 5px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1em;
  box-sizing: border-box;
}

.submit-button {
  width: 40%;
  padding: 12px;
  background-color: black;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px;
  margin: 20px auto 10px; /* Centrado horizontal */
  display: block;  /* Hace que el botón sea un bloque para que se pueda centrar */
}

.submit-button:hover {
  background-color: grey;
}

.close-button {
  width: 40%;
  padding: 12px;
  background-color: #e23134;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 10px;
  margin: 20px auto 10px; /* Centrado horizontal */
  display: block;  /* Hace que el botón sea un bloque para que se pueda centrar */
}

.close-button:hover {
  background-color: #c31d20;
}

.update-button, .goal-button {
  margin-top: auto;  /* Empuja el botón hacia abajo dentro de su contenedor */
  padding: 12px 20px;
  background-color: black;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease, background-color 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.update-button:hover, .goal-button:hover {
  background-color: grey;
  transform: scale(1.05);
}

.confirmation-message {
  color: white;
  text-align: center;
  padding: 10px;
  border-radius: 5px;
  margin-top: 250px;
  font-size: 18px;
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
}

.confirmation-message.success {
  background-color: #2ecc71; /* verde */
}

.confirmation-message.error {
  background-color: #e74c3c; /* rojo */
}

.icon-check {
  width: 20px;
  height: 20px;
  margin-right: 10px;
  vertical-align: middle;
  fill: white;
}

.progress-container {
  width: 200px;
  text-align: center;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background-color: #ddd;
  border-radius: 5px;
  margin-top: 10px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #2ecc71;
  transition: width 0.3s ease;
}

.goal-container {
  max-width: 600px;
  margin: auto;
  margin-top: 40px;
  padding: 20px;
  border: 2px solid #ccc;
  border-radius: 12px;
  text-align: center;
  background-color: #f9f9f9;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  font-size: 16px;
}

.goal-container button {
  margin: 10px 5px;
  padding: 8px 12px;
}

.small-input {
  margin: 10px 5px;
  padding: 8px 12px;
  width: 50px;
  padding: 5px;
  font-size: 16px;
}

.books-read {
  margin-top: 10px;
}

.progress-container {
  margin-top: 20px;
  width: 100%;
  max-width: 300px;
  text-align: center;
  margin-left: auto;
  margin-right: auto;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background-color: #ddd;
  border-radius: 5px;
  margin-top: 10px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #2ecc71;
  transition: width 0.3s ease;
}

.export-import-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dropdown {
  position: relative;
  display: inline-block;
}

.export-button {
    background-color: #222; 
    color: white;
    border: 1px solid #444;
    border-radius: 5px;
    padding: 10px 15px;
    cursor: pointer;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 5px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
    transition: background-color 0.3s ease, transform 0.2s ease;
}

.export-button:hover {
    background-color: #444;
    transform: scale(1.05);
}

.dropdown-icon {
  width: 12px;
  height: 12px;
  fill: white;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.dropdown-item {
  padding: 10px 15px;
  background-color: white;
  color: #333;
  border: none;
  text-align: left;
  cursor: pointer;
  font-size: 14px;
}

.dropdown-item:hover {
  background-color: #f4f4f4;
}
</style>
