package ifrn.pi.eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ifrn.pi.eventos.models.Convidado;
import ifrn.pi.eventos.models.Evento;
import ifrn.pi.eventos.repositories.ConvidadoRepository;
import ifrn.pi.eventos.repositories.EventoRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/eventos")
public class EventosController {

	@Autowired
	private EventoRepository er;
	@Autowired
	private ConvidadoRepository cr;

	@GetMapping("/form")
	public String form(Evento evento) {
		return "eventos/formEvento";
	}

	public String cadastrarEvento(Evento evento) {
		System.out.println("Cadastrando");

		System.out.println("Nome:" + evento.getNome());
		System.out.println("Local:" + evento.getLocal());
		System.out.println("Data:" + evento.getData());
		System.out.println("Horário:" + evento.getHorario());

		return "home";
	}

	@PostMapping
	public String salvar(@Valid Evento evento, BindingResult result) {
	public String salvar(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {

		if(result.hasErrors()) {
			return form(evento);
		}

		System.out.println(evento);
		er.save(evento);

		attributes.addFlashAttribute("mensagem", "Evento salvo com sucesso!");
		
		return "redirect:/eventos";
	}

	@GetMapping
	public ModelAndView listar() {

		List<Evento> eventos = er.findAll();
		ModelAndView mv = new ModelAndView("eventos/lista");
		mv.addObject("eventos", eventos);
		return mv;
	}

	@GetMapping("/{id}")
	public ModelAndView detalhar(@PathVariable Long id, Convidado convidado) {
		ModelAndView md = new ModelAndView();
		Optional<Evento> opt = er.findById(id);
		
		if (opt.isEmpty()) {
			md.setViewName("redirect:/eventos");
			return md;
		}
	    ModelAndView md = new ModelAndView();
	    Optional<Evento> opt = er.findById(id);

		md.setViewName("eventos/detalhes");
		Evento evento = opt.get();
		md.addObject("evento", evento);
	    if (opt.isEmpty()) {
	        md.setViewName("redirect:/eventos");
	        return md;
	    }

		List<Convidado> convidados = cr.findByEvento(evento);
		md.addObject("convidados", convidados);
		
		return md;
	    md.setViewName("eventos/detalhes");
	    Evento evento = opt.get();
	    md.addObject("evento", evento);

	    List<Convidado> convidados = cr.findByEvento(evento);
	    md.addObject("convidados", convidados);

	    return md;
	}

	@PostMapping("/{idEvento}")
	public String salvarConvidado(@PathVariable Long idEvento, @Valid Convidado convidado, BindingResult result) {
		
		if (result.hasErrors()) {
	        return "redirect:/eventos/{idEvento}";
	public ModelAndView salvarConvidado(@PathVariable Long idEvento, @Valid Convidado convidado, BindingResult result, RedirectAttributes attributes) {
	    ModelAndView md = new ModelAndView();

	    Optional<Evento> opt = er.findById(idEvento);
	    if (opt.isEmpty()) {
	        md.setViewName("redirect:/eventos");
	        return md;
	    }
		
		System.out.println("Id do evento: " + idEvento);
		System.out.println(convidado);
		
		Optional<Evento> opt = er.findById(idEvento);
		if(opt.isEmpty()) {
			return "redirect:/eventos";
		}
		
		Evento evento = opt.get();
		convidado.setEvento(evento);
		
		cr.save(convidado);
		
		return "redirect:/eventos/{idEvento}";

	    Evento evento = opt.get();

	    if (result.hasErrors()) {
	        md.setViewName("eventos/detalhes");
	        md.addObject("evento", evento);
	        md.addObject("convidados", cr.findByEvento(evento));
	        return md;
	    }

	    if (convidado.getId() != null && convidado.getId().equals(idEvento)) {
	        convidado.setId(null);
	    }

	    convidado.setEvento(evento);
	    cr.save(convidado);
	    attributes.addFlashAttribute("mensagem", "Convidado salvo com sucesso!");

	    md.setViewName("redirect:/eventos/" + idEvento);
	    return md;
	}

	@GetMapping("/{id}/selecionar")
	public ModelAndView selecionarEvento(@PathVariable Long id) {
		ModelAndView md = new ModelAndView();
		Optional<Evento> opt = er.findById(id);
		if(opt.isEmpty()) {
			md.setViewName("redirect:/eventos");
			return md;
		}

		Evento evento = opt.get();
		md.setViewName("eventos/formEvento");
		md.addObject("evento", evento);

		return md;
	}

	@GetMapping("/{idEvento}/convidados/{idConvidado}/selecionar")
	public ModelAndView selecionarConvidado(@PathVariable Long idEvento, @PathVariable Long idConvidado) {
	    ModelAndView md = new ModelAndView();

	    Optional<Evento> optEvento = er.findById(idEvento);
	    Optional<Convidado> optConvidado = cr.findById(idConvidado);

	    if(optEvento.isEmpty() || optConvidado.isEmpty() ) {
	        md.setViewName("redirect:/eventos");
	        return md;
	    }

	    Evento evento = optEvento.get();
	    Convidado convidado = optConvidado.get();

	    if(evento.getId() != convidado.getEvento().getId()) {
	        md.setViewName("redirect:/eventos");
	        return md;
	    }

	    md.setViewName("eventos/detalhes");
	    md.addObject("convidado", convidado); // CORRIGIDO AQUI (c minúsculo)
	    md.addObject("evento", evento);
	    md.addObject("convidados", cr.findByEvento(evento));

	    return md;
	}

	@GetMapping("/{id}/remover")
	public String apagarEvento(@PathVariable Long id) {
	public String apagarEvento(@PathVariable Long id, RedirectAttributes attributes) {

		Optional<Evento> opt = er.findById(id);

		if(!opt.isEmpty()) {
			// apagar

			Evento evento = opt.get();

			List<Convidado> convidados = cr.findByEvento(evento);

			cr.deleteAll(convidados);
			er.delete(evento);
			attributes.addFlashAttribute("mensagem", "Evento removido com sucesso!");
		}

		return "redirect:/eventos";
	}

	@GetMapping("/{idEvento}/convidados/{idConvidado}/remover")
	public String apagarConvidado(@PathVariable Long idEvento, @PathVariable Long idConvidado) {
	public String apagarConvidado(@PathVariable Long idEvento, @PathVariable Long idConvidado, RedirectAttributes attributes) {

	    Optional<Convidado> optConvidado = cr.findById(idConvidado);

	    if (optConvidado.isPresent()) {
	        Convidado convidado = optConvidado.get();
	        // Garante que o convidado realmente pertence a este evento antes de deletar
	       
	        if (convidado.getEvento().getId().equals(idEvento)) {
	            cr.delete(convidado);
	            attributes.addFlashAttribute("mensagem", "Convidado removido com sucesso!");
	        }
	    }

	    return "redirect:/eventos/" + idEvento;
	}

}