package userRepo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {

	private final UserRepository repository;

	UserController(UserRepository repository) {
		this.repository = repository;
	}


	@GetMapping("/users")
	CollectionModel<EntityModel<User>> all() {

		List<EntityModel<User>> users = repository.findAll().stream()
				.map(user -> EntityModel.of(user,
						linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
						linkTo(methodOn(UserController.class).all()).withRel("users")))
				.collect(Collectors.toList());

		return CollectionModel.of(users, linkTo(methodOn(UserController.class).all()).withSelfRel());
	}

	@PostMapping("/users")
	User newUser(@RequestBody User user) {
		return repository.save(user);
	}


	@GetMapping("/users/{id}")
	EntityModel<User> one(@PathVariable Long id) {

		User user = repository.findById(id) //
				.orElseThrow(() -> new UserNotFoundException(id));

		return EntityModel.of(user, //
				linkTo(methodOn(UserController.class).one(id)).withSelfRel(),
				linkTo(methodOn(UserController.class).all()).withRel("user"));
	}

	@PutMapping("/users/{id}")
	User updateUser(@RequestBody User newUser, @PathVariable Long id) {

		return repository.findById(id) //
				.map(user -> {
					user.setName(newUser.getName());
					user.setRole(newUser.getRole());
					return repository.save(user);
				}) //
				.orElseGet(() -> {
					newUser.setId(id);
					return repository.save(newUser);
				});
	}

	@DeleteMapping("/users/{id}")
	void deleteUser(@PathVariable Long id) {
		repository.deleteById(id);
	}
}
